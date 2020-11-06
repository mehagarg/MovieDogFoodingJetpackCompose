package com.example.myapplication.model

data class Movie(
    val title : String,
    val posterUrl: String,
    val bgUrl: String,
    val chips: List<String>
)


val movies = listOf(
    Movie(
        title = "Good Boys",
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMTc1NjIzODAxMF5BMl5BanBnXkFtZTgwMTgzNzk1NzM@._V1_.jpg",
        bgUrl = "https://m.media-amazon.com/images/M/MV5BMTc1NjIzODAxMF5BMl5BanBnXkFtZTgwMTgzNzk1NzM@._V1_.jpg",
//        color = Color.Red,
        chips = listOf("Action", "Drama", "History"),
//        actors = listOf(
//            MovieActor("Jaoquin Phoenix", "https://image.tmdb.org/t/p/w138_and_h175_face/nXMzvVF6xR3OXOedozfOcoA20xh.jpg"),
//            MovieActor("Robert De Niro", "https://image.tmdb.org/t/p/w138_and_h175_face/cT8htcckIuyI1Lqwt1CvD02ynTh.jpg"),
//            MovieActor("Zazie Beetz", "https://image.tmdb.org/t/p/w138_and_h175_face/sgxzT54GnvgeMnOZgpQQx9csAdd.jpg")
//        ),
//        introduction = "During the 1980s, a failed stand-up comedian is driven insane and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure."
    ),
    Movie(
        title = "Joker",
        posterUrl = "https://i.etsystatic.com/15963200/r/il/25182b/2045311689/il_794xN.2045311689_7m2o.jpg",
        bgUrl = "https://images-na.ssl-images-amazon.com/images/I/61gtGlalRvL._AC_SY741_.jpg",
//        color = Color.Blue,
        chips = listOf("Action", "Drama", "History"),
//        actors = listOf(
//            MovieActor("Jaoquin Phoenix", "https://image.tmdb.org/t/p/w138_and_h175_face/nXMzvVF6xR3OXOedozfOcoA20xh.jpg"),
//            MovieActor("Robert De Niro", "https://image.tmdb.org/t/p/w138_and_h175_face/cT8htcckIuyI1Lqwt1CvD02ynTh.jpg"),
//            MovieActor("Zazie Beetz", "https://image.tmdb.org/t/p/w138_and_h175_face/sgxzT54GnvgeMnOZgpQQx9csAdd.jpg")
//        ),
//        introduction = "During the 1980s, a failed stand-up comedian is driven insane and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure."
    ),
    Movie(
        title = "The Hustle",
        posterUrl = "https://m.media-amazon.com/images/M/MV5BMTc3MDcyNzE5N15BMl5BanBnXkFtZTgwNzE2MDE0NzM@._V1_.jpg",
        bgUrl = "https://m.media-amazon.com/images/M/MV5BMTc3MDcyNzE5N15BMl5BanBnXkFtZTgwNzE2MDE0NzM@._V1_.jpg",
//        color = Color.Yellow,
        chips = listOf("Action", "Drama", "History"),
//        actors = listOf(
//            MovieActor("Jaoquin Phoenix", "https://image.tmdb.org/t/p/w138_and_h175_face/nXMzvVF6xR3OXOedozfOcoA20xh.jpg"),
//            MovieActor("Robert De Niro", "https://image.tmdb.org/t/p/w138_and_h175_face/cT8htcckIuyI1Lqwt1CvD02ynTh.jpg"),
//            MovieActor("Zazie Beetz", "https://image.tmdb.org/t/p/w138_and_h175_face/sgxzT54GnvgeMnOZgpQQx9csAdd.jpg")
//        ),
//        introduction = "During the 1980s, a failed stand-up comedian is driven insane and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure."
    ),

    Movie(
        title = "Challenger",
        posterUrl = "https://img.pngio.com/movie-poster-png-102-images-in-collection-page-3-movie-poster-png-1159_1500.png",
        bgUrl = "https://img.pngio.com/movie-poster-png-102-images-in-collection-page-3-movie-poster-png-1159_1500.png",
//        color = Color.Yellow,
        chips = listOf("Action", "Drama", "History"),
//        actors = listOf(
//            MovieActor("Jaoquin Phoenix", "https://image.tmdb.org/t/p/w138_and_h175_face/nXMzvVF6xR3OXOedozfOcoA20xh.jpg"),
//            MovieActor("Robert De Niro", "https://image.tmdb.org/t/p/w138_and_h175_face/cT8htcckIuyI1Lqwt1CvD02ynTh.jpg"),
//            MovieActor("Zazie Beetz", "https://image.tmdb.org/t/p/w138_and_h175_face/sgxzT54GnvgeMnOZgpQQx9csAdd.jpg")
//        ),
//        introduction = "During the 1980s, a failed stand-up comedian is driven insane and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure."
    ),
    Movie(
        title = "Moonlight",
        posterUrl = "https://cdn.pastemagazine.com/www/articles/main-image.jpg",
        bgUrl = "https://cdn.pastemagazine.com/www/articles/main-image.jpg",
//        color = Color.Yellow,
        chips = listOf("Action", "Drama", "History"),
//        actors = listOf(
//            MovieActor("Jaoquin Phoenix", "https://image.tmdb.org/t/p/w138_and_h175_face/nXMzvVF6xR3OXOedozfOcoA20xh.jpg"),
//            MovieActor("Robert De Niro", "https://image.tmdb.org/t/p/w138_and_h175_face/cT8htcckIuyI1Lqwt1CvD02ynTh.jpg"),
//            MovieActor("Zazie Beetz", "https://image.tmdb.org/t/p/w138_and_h175_face/sgxzT54GnvgeMnOZgpQQx9csAdd.jpg")
//        ),
//        introduction = "During the 1980s, a failed stand-up comedian is driven insane and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure."
    )
)