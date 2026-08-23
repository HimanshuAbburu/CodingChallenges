# Use an official Python runtime as a parent image
FROM python:3.15.0b3-trixie

# Set the working directory in the container
WORKDIR /app

# Copy the local directory contents into the container at /app
COPY . /app
RUN pip3 install -r requirements.txt



# Run Python script when the container launches
CMD ["python", "main.py"]

# To run in the docker instance paste the below in the terminal
# 1. docker build --tag main .
# 2. docker run app 