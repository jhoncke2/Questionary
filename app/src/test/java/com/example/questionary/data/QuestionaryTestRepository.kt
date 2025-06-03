package com.example.questionary.data

object QuestionaryTestRepository {
    val questionary = listOf(
        Question(
            "1",
            "¿Cuántos hijos quiero tener?",
            listOf(
                Answer(
                    "3",
                    false
                ),
                Answer(
                    "1",
                    false
                ),
                Answer(
                    "2",
                    true
                ),
                Answer(
                    "Ninguno, creo que los hijos no son necesarios",
                    false
                )
            )
        ),
        Question(
            "2",
            "¿Cuál es mi libro de la Biblia favorito?",
            listOf(
                Answer(
                    "Génesis",
                    false
                ),
                Answer(
                    "Romanos",
                    true
                ),
                Answer(
                    "Apocalipsis",
                    false
                ),
                Answer(
                    "Eclesiástico",
                    false
                )
            )
        ),
        Question(
            "3",
            "¿Cuál es mi comida favorita?",
            listOf(
                Answer(
                    "Pasta con albondiga",
                    false
                ),
                Answer(
                    "Ajiaco Santafereño",
                    false
                ),
                Answer(
                    "Bandeja Paisa",
                    true
                ),
                Answer(
                    "Mazamorra",
                    false
                )
            )
        ),
        Question(
            "4",
            "¿Cuál de estos fue uno de mis apodos en secundaria?",
            listOf(
                Answer(
                    "Zombie",
                    true
                ),
                Answer(
                    "JhonnyNeitor",
                    false
                ),
                Answer(
                    "Patas de Pingüino",
                    false
                ),
                Answer(
                    "El papu",
                    false
                )
            )
        ),
        Question(
            "5",
            "¿Cuál es mi peor miedo en la tierra?",
            listOf(
                Answer(
                    "Que me persiga It",
                    false
                ),
                Answer(
                    "Casarme con una feminista",
                    true
                ),
                Answer(
                    "Que mis sobrinos se vuelvan socialistas",
                    false
                ),
                Answer(
                    "Que Macarena se vuelva transgénero",
                    false
                )
            )
        )
    )
}