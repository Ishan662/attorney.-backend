package com.example.backend.service.FirebaseChat;

import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.example.backend.model.user.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FirebaseChatService {

    /**
     * Creates a new chat channel document in Firestore for a case.
     * The document ID will be the same as the case's UUID.
     */
    public String createCaseChannel(UUID caseId, String caseTitle, List<User> members) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            String channelId = caseId.toString();

            // Prepare the data to be stored in the Firestore document
            Map<String, Object> channelData = new HashMap<>();
            channelData.put("caseTitle", caseTitle);
            channelData.put("createdAt", System.currentTimeMillis()); // Store creation timestamp

            // Store a list of member UUIDs for easy frontend access rules
            List<String> memberIds = members.stream()
                    .map(user -> user.getId().toString())
                    .collect(Collectors.toList());
            channelData.put("members", memberIds);

            // Create a new document in the "channels" collection.
            // If the collection doesn't exist, Firestore creates it automatically.
            db.collection("channels").document(channelId).set(channelData).get();

            System.out.println("Successfully created Firestore chat channel: " + channelId);
            return channelId;
        } catch (Exception e) {
            // Use a proper logger in a real application (e.g., SLF4J)
            System.err.println("Error creating Firestore channel for case: " + caseId + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a single user to an existing Firestore chat channel.
     * This is used when an invited client accepts their invitation.
     *
     * @param channelId The ID of the channel (which is the case UUID).
     * @param userToAdd The User object to add to the channel's member list.
     */
    public void addUserToChannel(String channelId, User userToAdd) {
        if (channelId == null || userToAdd == null) {
            System.err.println("Cannot add user to channel: channelId or user is null.");
            return;
        }

        try {
            Firestore db = FirestoreClient.getFirestore();

            // Use FieldValue.arrayUnion to atomically add the new user's ID
            // to the 'members' array in the Firestore document.
            // This prevents duplicates if the operation is ever run more than once.
            db.collection("channels").document(channelId)
                    .update("members", FieldValue.arrayUnion(userToAdd.getId().toString()));

            System.out.println("Successfully added user " + userToAdd.getId() + " to channel " + channelId);
        } catch (Exception e) {
            System.err.println("Error adding user " + userToAdd.getId() + " to channel " + channelId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}