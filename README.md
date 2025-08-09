# Database

---

## Option 1: Use AWS RDS

1. Comment out: `# DataSource (Docker-PostgreSQL) inside application.properties`
2. Uncomment: `## DataSource (aws-postgresql) inside application.properties`
3. Make sure your AWS credentials and DB are correctly set up.

---

## Option 2: Use Docker PostgreSQL

1. Ensure Docker is running.(This will take upto 400mb to 500mb)
2. Comment out: `## DataSource (aws-postgresql) inside application.properties`
3. Uncomment: `# DataSource (Docker-PostgreSQL) inside application.properties`
4. Run DB:
   ```bash 
    docker container run -p 5433:5432 -e POSTGRES_USER=attorney -e POSTGRES_PASSWORD=atto123  -e POSTGRES_DB=attorney -d --name attorney-db postgres:15
   
5. To stop and start the containers run
   1. Stop: docker container stop attorney-db
   2. Start: docker container start attorney-db
   3. Or use docker-desktop

server pass : Atto#123
server admin: attorney