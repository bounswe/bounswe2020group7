Set Up All System (Frontend-Backend_Database) With Docker-Compose 

    In this approach it is possible to set up backend, frontend and database server together. To set up with Docker-Compose use the following commands:

    1. Install Docker and Docker-Compose

    2. Adjust the Frontend Host Name: 
    IMPORTANT: This step is only required if you want to deploy frontend into a remote machine. Currently the delivered code is arraged to run in localhost. 
    Change the FRONTEND_HOSTNAME variable in the <Path of Repository>/platon/config.py file.
        Example: FRONTEND_HOSTNAME = "http://ec2-3-120-98-39.eu-central-1.compute.amazonaws.com/"
    
    3. Restore Database Dump(If you want an empty database, skip this step)

        3.1 cd <Path of Repository>/platon

        3.2 sudo bash restore_database.sh
    
    4. Run All System

        4.1 cd <Path of Repository>/platon

        4.2 sudo bash run.sh

To achive API Documentation *Please Run the System* and go to `http://127.0.0.1:5000/` or `http://127.0.0.1:5000/documentation`

Database username and password can be found in the path: <Path of Repository>/platon/docker-compose.yml
	MYSQL_DATABASE=platondb 
	MYSQL_ROOT_PASSWORD=rootpassword 
	MYSQL_USER=root