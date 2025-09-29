# Movie Manager

## Overview

A simple movie search and watchlist manager application built with Java and Swing. This application allows users to search for movies, view their details, and manage a personal watchlist. It utilizes several APIs to fetch movie data, trending shows, and streaming information.

## Features

- Search for movies by title.
- View movie details, including poster, plot, director, actors, and IMDb rating.
- View streaming information for movies.
- Add movies to a watchlist.
- View and manage the watchlist.

## API Usage

This application uses the following APIs:

- **OMDB API:** Used to fetch detailed movie information, such as plot, director, actors, and IMDb rating, based on a movie title search.
- **TMDb API:** Used to fetch a list of trending shows, which are displayed on the home screen of the application.
- **Watchmode API:** Used to find out where to stream movies online. It provides information about which streaming services (e.g., Netflix, Hulu, Amazon Prime) offer a particular movie for streaming, rental, or purchase.

## Technologies Used

- Java
- Swing (for the UI)
- SQLite (for the database)
- OMDB API (for movie data)
- TMDb API (for trending shows)
- Watchmode API (for streaming information)
- Gson (for parsing JSON)
- java-dotenv (for managing environment variables)
