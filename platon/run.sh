echo "Starting Frontend Server"
cd frontend
docker rm -f $(sudo docker ps -a -q)
docker image build -t frontend .
docker run -p 80:3000 -d frontend
cd ..
echo "Frontend Server is Started"
echo "Starting Backend and Database Server"
docker-compose down
docker-compose up --build -d
echo "Backend and Database Server is Started"
