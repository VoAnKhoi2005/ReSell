FROM python:3.11-slim

# Install system dependencies only if needed
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .

# Install pip dependencies
RUN pip install --upgrade pip \
 && pip install --no-cache-dir -r requirements.txt \
 && apt-get purge -y build-essential \
 && apt-get autoremove -y \
 && rm -rf /root/.cache /var/lib/apt/lists/*

WORKDIR /app
COPY . .

CMD ["python", "main.py"]
