# Fingers on Buzzers

## About

A social web app, with the concept of allowing you and your friends to participate in a TV-style quiz show.

Intended to be used by a number of players in a voice chat with each other.

## Requirements

* [Docker for Windows](https://docs.docker.com/desktop/install/windows-install/)
* [IntelliJ](https://www.jetbrains.com/idea/)
* [Java 17](https://adoptium.net/en-GB/temurin/releases/)
* [Node 18](https://nodejs.org/en)

## Setup

It is recommended to open the frontend and backend as separate projects, I used IntelliJ for backend and WebStorm for
the frontend.

**Start the backend**

1. Start the containers in `devtools/docker-compose.yml` via IntelliJ
2. Set the active profile for the `BackendApplication` run configuration to `development`
3. Start the `BackendApplication`

**Start the frontend**

1. Install NPM dependencies via `npm install`
2. Start the frontend using `npm run dev`

## Using the App

When running locally the app will be accessible by http://127.0.0.1:5173/ via a browser.
