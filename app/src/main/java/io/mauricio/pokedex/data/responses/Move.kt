package io.mauricio.pokedex.data.responses

data class Move(
    val move: MoveX,
    val version_group_details: List<VersionGroupDetail>
)