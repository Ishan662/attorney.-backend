// >> In a NEW file: service/TeamService.java
package com.example.backend.service;

import com.example.backend.dto.team.*;
import com.example.backend.dto.userDTO.UserDTO; // Make sure your UserDTO path is correct
import com.example.backend.mapper.UserMapper;   // Make sure your UserMapper path is correct
import com.example.backend.model.AppRole;
import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseMember;
import com.example.backend.model.junior.JuniorProfile;
import com.example.backend.model.salary.SalaryPayment;
import com.example.backend.model.user.User;
import com.example.backend.repositories.CaseMemberRepository;
import com.example.backend.repositories.JuniorProfileRepository;
import com.example.backend.repositories.SalaryPaymentRepository;
import com.example.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final JuniorProfileRepository juniorProfileRepository;
    private final SalaryPaymentRepository salaryPaymentRepository;

    private final CaseMemberRepository caseMemberRepository;

    @Autowired
    public TeamService(UserRepository userRepository, UserMapper userMapper, JuniorProfileRepository juniorProfileRepository, SalaryPaymentRepository salaryPaymentRepository, CaseMemberRepository caseMemberRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.juniorProfileRepository = juniorProfileRepository;
        this.salaryPaymentRepository = salaryPaymentRepository;
        this.caseMemberRepository = caseMemberRepository;
    }

    /**
     * Fetches all users with the 'JUNIOR' role from the currently
     * authenticated lawyer's firm.
     */
    public List<UserDTO> getJuniorsForCurrentFirm() {
        // 1. Get the currently logged-in user (the lawyer).
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found."));

        // 2. Find all users who share the same firm ID and have the role 'JUNIOR'.
        //    (You must add the 'findByFirmIdAndRole' method to your UserRepository).
        List<User> juniors = userRepository.findByFirmIdAndRole(currentUser.getFirm().getId(), AppRole.JUNIOR);

        // 3. Convert the list of User entities to a list of UserDTOs to send to the frontend.
        return juniors.stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public User updateUserStatus(UUID userIdToUpdate, UpdateUserStatusRequestDTO request) {
        User lawyer = getCurrentLawyer();
        User userToUpdate = findUserInFirm(userIdToUpdate, lawyer);

        userToUpdate.setStatus(request.getNewStatus());
        return userRepository.save(userToUpdate);
    }

    @Transactional
    public JuniorProfile updateJuniorSalary(UUID juniorId, UpdateSalaryRequestDTO request) {
        User lawyer = getCurrentLawyer();
        User junior = findUserInFirm(juniorId, lawyer);

        if (junior.getRole() != AppRole.JUNIOR) {
            throw new IllegalArgumentException("Salary can only be set for JUNIOR users.");
        }

        // --- CORRECTED LOGIC ---

        // 1. Try to find the existing profile.
        JuniorProfile profile = juniorProfileRepository.findById(juniorId)
                .orElseGet(() -> {
                    // 2. If it does NOT exist, create a new one.
                    JuniorProfile newProfile = new JuniorProfile();
                    // 3. Set the User object. This is the key to the relationship.
                    //    The @MapsId annotation will automatically use the User's ID
                    //    as the ID for this new JuniorProfile.
                    newProfile.setUser(junior);
                    return newProfile;
                });

        // 4. Whether it's a new or existing profile, set the salary.
        profile.setMonthlySalary(request.getNewMonthlySalary());

        // 5. Save the profile. JPA will now correctly perform an INSERT for a new
        //    profile or an UPDATE for an existing one.
        return juniorProfileRepository.save(profile);
    }

    @Transactional
    public SalaryPayment recordSalaryPayment(UUID juniorId, RecordSalaryPaymentRequestDTO request) {
        User lawyer = getCurrentLawyer();
        User junior = findUserInFirm(juniorId, lawyer);

        if (junior.getRole() != AppRole.JUNIOR) {
            throw new IllegalArgumentException("Salary payments can only be recorded for JUNIOR users.");
        }

        SalaryPayment payment = new SalaryPayment();
        payment.setJunior(junior);
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setNotes(request.getNotes());
        payment.setRecordedBy(lawyer);

        return salaryPaymentRepository.save(payment);
    }

    // --- HELPER METHODS FOR SECURITY AND REUSE ---

    private User getCurrentLawyer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String firebaseUid = authentication.getName();
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new UsernameNotFoundException("Current lawyer not found."));
    }

    private User findUserInFirm(UUID userIdToFind, User lawyer) {
        User userToFind = userRepository.findById(userIdToFind)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userIdToFind));

        if (lawyer.getFirm() == null || !lawyer.getFirm().getId().equals(userToFind.getFirm().getId())) {
            throw new SecurityException("Forbidden: You can only manage users within your own firm.");
        }
        return userToFind;
    }

    // --- NEW "READ" METHODS FOR YOUR UI ---

    @Transactional(readOnly = true)
    public List<JuniorLawyerOverviewDTO> getJuniorsOverview() {
        User lawyer = getCurrentLawyer();
        List<User> juniors = userRepository.findByFirmIdAndRole(lawyer.getFirm().getId(), AppRole.JUNIOR);

        // Fetch all profiles in one go for efficiency
        List<UUID> juniorIds = juniors.stream().map(User::getId).collect(Collectors.toList());
        Map<UUID, JuniorProfile> profiles = juniorProfileRepository.findAllById(juniorIds).stream()
                .collect(Collectors.toMap(JuniorProfile::getId, Function.identity()));

        return juniors.stream().map(junior -> {
            JuniorLawyerOverviewDTO dto = new JuniorLawyerOverviewDTO();
            dto.setId(junior.getId());
            dto.setFirstName(junior.getFirstName());
            dto.setLastName(junior.getLastName());
            dto.setEmail(junior.getEmail());
            dto.setPhoneNumber(junior.getPhoneNumber());
            dto.setStatus(junior.getStatus());
            dto.setCreatedAt(junior.getCreatedAt());

            JuniorProfile profile = profiles.get(junior.getId());
            if (profile != null) {
                dto.setMonthlySalary(profile.getMonthlySalary());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClientOverviewDTO> getClientsOverview() {
        User lawyer = getCurrentLawyer();
        List<User> clients = userRepository.findByFirmIdAndRole(lawyer.getFirm().getId(), AppRole.CLIENT);

        return clients.stream().map(client -> {
            ClientOverviewDTO dto = new ClientOverviewDTO();
            dto.setId(client.getId());
            dto.setFirstName(client.getFirstName());
            dto.setLastName(client.getLastName());
            dto.setEmail(client.getEmail());
            dto.setPhoneNumber(client.getPhoneNumber());
            dto.setStatus(client.getStatus());
            dto.setCreatedAt(client.getCreatedAt());

            // Efficiently count cases using the new repository method
            dto.setAssignedCasesCount(caseMemberRepository.countByUserId(client.getId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDetailDTO getUserDetails(UUID userId) {
        // 1. Get the current lawyer and verify they have permission to view this user.
        User lawyer = getCurrentLawyer();
        User userToView = findUserInFirm(userId, lawyer); // Your existing secure helper method

        // 2. Start building the main DTO with the user's basic information.
        UserDetailDTO dto = new UserDetailDTO();
        dto.setId(userToView.getId());
        dto.setFirstName(userToView.getFirstName());
        dto.setLastName(userToView.getLastName());
        dto.setEmail(userToView.getEmail());
        dto.setPhoneNumber(userToView.getPhoneNumber());
        dto.setRole(userToView.getRole());
        dto.setStatus(userToView.getStatus());
        dto.setCreatedAt(userToView.getCreatedAt());

        // 3. Fetch all case memberships for this user.
        List<CaseMember> memberships = caseMemberRepository.findByUserId(userToView.getId());

        // 4. Loop through the memberships to get each case and map it to a CaseInfoDTO.
        List<UserDetailDTO.CaseInfoDTO> caseInfos = new ArrayList<>();
        for (CaseMember member : memberships) {
            Case aCase = member.getaCase(); // Get the associated Case entity

            if (aCase != null) {
                UserDetailDTO.CaseInfoDTO caseDto = new UserDetailDTO.CaseInfoDTO();
                caseDto.setCaseId(aCase.getId());
                caseDto.setCaseTitle(aCase.getCaseTitle());
                caseDto.setCaseNumber(aCase.getCaseNumber());
                caseDto.setStatus(aCase.getStatus());
                caseInfos.add(caseDto);
            }
        }

        // 5. Set the list of cases on the main DTO.
        dto.setAssignedCases(caseInfos);

        // 6. Return the complete DTO.
        return dto;
    }

    @Transactional(readOnly = true)
    public List<TeamMemberSelectDTO> getTeamMembersForSelection(AppRole role) {
        User lawyer = getCurrentLawyer();

        // Find all users in the firm with the specified role who are active
        List<User> users = userRepository.findByFirmIdAndRole(lawyer.getFirm().getId(), role);

        // Map them to the new lightweight DTO
        return users.stream().map(user -> {
            TeamMemberSelectDTO dto = new TeamMemberSelectDTO();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            return dto;
        }).collect(Collectors.toList());
    }


}