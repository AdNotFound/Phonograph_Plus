/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.repo.mediastore

import player.phonograph.model.Genre
import player.phonograph.model.Song
import player.phonograph.repo.loader.IGenres
import player.phonograph.repo.mediastore.loaders.GenreLoader
import android.content.Context

object MediaStoreGenres : IGenres {

    override fun all(context: Context): List<Genre> = GenreLoader.all(context)

    override fun id(context: Context, id: Long): Genre? = GenreLoader.id(context, id)

    override fun songs(context: Context, genreId: Long): List<Song> = GenreLoader.genreSongs(context, genreId)

}