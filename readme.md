# MSM-Hacks-Muppets

A project to create a **private server** for the game **My Muppets Show** — a spin-off of the popular game **My Singing Monsters**, with enhancements and mods to improve the gameplay.

The game was shut down 10 years ago, but with this server, you can bring it back and enjoy playing with friends!

## Description

This repository contains the code for a **private server** for the game **My Muppets Show**, allowing you to host a game server on your computer and connect to it using the client app.

The project is written in **Java** and requires **Java 17** or newer for installation.

**MSM-Hacks-Muppets** includes various mods for improving the gameplay, such as unlocking all characters, adding extra resources, and custom scenes.

## Installation

Follow these steps to set up the server:

### 1. Clone the repository
```bash
git clone https://github.com/MSM-Hacks/MSM-Hacks-Muppets
```

### 2. Navigate to the repository directory
```bash
cd MSM-Hacks-Muppets
```

### 3. Build the project using Maven or Gradle

#### Maven:
```bash
mvn clean install
```

#### Gradle:
```bash
./gradlew build
```

### 4. Edit the configuration file

Configure the server parameters by editing the configuration file (e.g., `config.properties`). Make sure you set the correct IP address and port for the server connection.

### 5. Run the server
Start the server using the JAR file generated during the build process:
```bash
java -jar target/MSM-Hacks-Muppets.jar
```

## Connecting to the Server

To connect to the server, you need to install the **My Muppets Show** app on your Android device. Follow these steps:

1. Open the **My Muppets Show** app on your device.
2. Go to the settings or connection page (if available).
3. Enter the IP address and port of the server as configured in the server settings.

**Note**: The exact connection steps may vary depending on the game version and interface.

## Mods and Enhancements

The project may include various mods to enhance the gameplay, such as:
- Unlocking all characters.
- Infinite resources.
- Custom scenes and musical performances.

These mods can be configured via configuration files or directly in the server code.

## Contributing

If you'd like to contribute to the project, follow these steps:

1. Fork the repository.
2. Make your changes in a new branch.
3. Submit a pull request with a clear description of your changes.
4. Ensure that the code adheres to the project standards and passes all tests.

## License

This project is licensed under the **MIT License**.

## Conclusion

With this server, you can bring back the game **My Muppets Show**, which was shut down 10 years ago. Enjoy the game and the enhancements provided by **MSM-Hacks-Muppets**!
