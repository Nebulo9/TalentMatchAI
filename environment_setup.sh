#!/bin/sh

if [[ -z "$(sudo docker ps -a | grep ollama)" ]]; then # Container does not exist
  echo "Creating Ollama container..."
  # Create container
  sudo docker run \
    --env=LD_LIBRARY_PATH=/usr/local/nvidia/lib:/usr/local/nvidia/lib64 \
    --env=NVIDIA_DRIVER_CAPABILITIES=compute,utility \
    --env=NVIDIA_VISIBLE_DEVICES=all \
    --env=OLLAMA_HOST=0.0.0.0:11434 \
    --volume=ollama:/root/.ollama \
    --network=bridge \
    -p 11434:11434 \
    --restart=no \
    --label='org.opencontainers.image.ref.name=ubuntu' \
    --label='org.opencontainers.image.version=24.04' \
    --runtime=runc \
    -d \
    --name ollama \
    ollama/ollama;

elif [[ -z "$(sudo docker ps | grep ollama)" ]]; then # Container exists but is stopped
  echo "Container ollama found, starting..."
  # Start the container
  sudo docker start ollama;
fi

# Pull the llama3.2:1b model
echo "Pulling llama3.2:1b model"
sudo docker exec -it ollama ollama pull llama3.2:1b

if [[ -z "$(sudo docker ps -a | grep redpanda)" ]]; then # Container does not exist
  echo "Creating Redpanda container..."
  # Create container
  docker run -d \
    --name redpanda \
    --hostname redpanda \
    -p 9092:9092 \
    -p 9644:9644 \
    -v redpanda-data:/var/lib/redpanda/data \
    docker.redpanda.com/redpandadata/redpanda:latest \
    redpanda start \
    --overprovisioned \
    --smp 1 \
    --memory 1G \
    --reserve-memory 0M \
    --node-id 0 \
    --check=false \
    --kafka-addr PLAINTEXT://0.0.0.0:9092 \
    --advertise-kafka-addr PLAINTEXT://localhost:9092

elif [[ -z "$(sudo docker ps | grep ollama)" ]]; then # Container exists but is stopped
  echo "Container Redpanda found, starting..."
  # Start the container
  sudo docker start redpanda;
fi

# Creating topic matching request
echo "Creating topic 'matching-requests'..."
docker exec redpanda rpk topic create matching-requests \
  --partitions 3 \
  --replicas 1

# Checking topic existance
echo "Checking topic existence"
docker exec redpanda rpk topic list
docker exec redpanda rpk topic describe matching-requests