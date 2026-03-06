@echo off

:: ─── OLLAMA ───────────────────────────────────────────────────────────────────

docker ps -a | findstr ollama >nul 2>&1
if errorlevel 1 (
    echo Creating Ollama container...
    docker run ^
        --env=LD_LIBRARY_PATH=/usr/local/nvidia/lib:/usr/local/nvidia/lib64 ^
        --env=NVIDIA_DRIVER_CAPABILITIES=compute,utility ^
        --env=NVIDIA_VISIBLE_DEVICES=all ^
        --env=OLLAMA_HOST=0.0.0.0:11434 ^
        --volume=ollama:/root/.ollama ^
        --network=bridge ^
        -p 11434:11434 ^
        --restart=no ^
        --label=org.opencontainers.image.ref.name=ubuntu ^
        --label=org.opencontainers.image.version=24.04 ^
        --runtime=runc ^
        -d ^
        --name ollama ^
        ollama/ollama
) else (
    docker ps | findstr ollama >nul 2>&1
    if errorlevel 1 (
        echo Container ollama found, starting...
        docker start ollama
    )
)

echo Pulling llama3.2:1b model...
docker exec ollama ollama pull llama3.2:1b

:: ─── REDPANDA ─────────────────────────────────────────────────────────────────

docker ps -a | findstr redpanda >nul 2>&1
if errorlevel 1 (
    echo Creating Redpanda container...
    docker run -d ^
        --name redpanda ^
        --hostname redpanda ^
        -p 9092:9092 ^
        -p 9644:9644 ^
        -v redpanda-data:/var/lib/redpanda/data ^
        docker.redpanda.com/redpandadata/redpanda:latest ^
        redpanda start ^
        --overprovisioned ^
        --smp 1 ^
        --memory 1G ^
        --reserve-memory 0M ^
        --node-id 0 ^
        --check=false ^
        --kafka-addr PLAINTEXT://0.0.0.0:9092 ^
        --advertise-kafka-addr PLAINTEXT://localhost:9092
) else (
    docker ps | findstr redpanda >nul 2>&1
    if errorlevel 1 (
        echo Container Redpanda found, starting...
        docker start redpanda
    )
)

:: ─── TOPIC ────────────────────────────────────────────────────────────────────

echo Creating topic 'matching-requests'...
docker exec redpanda rpk topic create matching-requests ^
    --partitions 3 ^
    --replicas 1

echo Checking topic existence...
docker exec redpanda rpk topic list
docker exec redpanda rpk topic describe matching-requests