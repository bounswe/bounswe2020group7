echo "Restore File System"
docker run --rm -v platon_platon_data:/volume -v $(pwd)/backup:/backup alpine sh -c "rm -rf /volume/* /volume/..?* /volume/.[!.]* ; tar -C /volume/ -xjf /backup/platon_data.tar.bz2"
echo "Restore MySQL Database"
docker run --rm -v platon_mysql_data:/volume -v $(pwd)/backup:/backup alpine sh -c "rm -rf /volume/* /volume/..?* /volume/.[!.]* ; tar -C /volume/ -xjf /backup/mysql_data.tar.bz2"
