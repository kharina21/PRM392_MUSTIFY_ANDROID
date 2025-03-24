import { sql, getConnection } from '../config/dbconfig.js';

export async function getLastestSongs(req, res) {
    const query = `SELECT TOP 10 [song_id]
                           ,[title]
                           ,[type_id]
                           ,[artist]
                           ,[album]
                           ,[duration]
                           ,[file_path]
                           ,[created_date]
                           ,[image]
                FROM [MUSTIFY].[dbo].[Song]
                ORDER BY [created_date] DESC;`;
    try {
        const pool = await getConnection();
        const result = await pool.request().query(query);
        const songs = result.recordset.map((row) => {
            return {
                song_id: row.song_id,
                title: row.title,
                type_id: row.type_id,
                artist: row.artist,
                album: row.album,
                duration: row.duration,
                file_path: row.file_path,
                created_date: row.created_date,
                image: row.image,
            };
        });
        if (songs.length > 0) {
            return res.json(songs);
        } else return res.status(404).json({ message: `failed to load songs` });
    } catch (err) {
        return res.status(500).json({ message: 'Interval server error', err });
    }
}
export async function getListSongByTitle(req, res) {
    const { title } = req.body;
    const query = `SELECT [song_id]
      ,[title]
      ,[type_id]
      ,[artist]
      ,[album]
      ,[duration]
      ,[file_path]
      ,[created_date]
      ,[image]
FROM [MUSTIFY].[dbo].[Song] 
WHERE title LIKE @title`;
    try {
        const pool = await getConnection();
        const result = await pool
            .request()
            .input('title', sql.NVarChar, `%${title}%`)
            .query(query);
        const songs = result.recordset.map((row) => {
            return {
                song_id: row.song_id,
                title: row.title,
                type_id: row.type_id,
                artist: row.artist,
                album: row.album,
                duration: row.duration,
                file_path: row.file_path,
                created_date: row.created_date,
                image: row.image,
            };
        });
        if (songs.length > 0) {
            return res.json(songs);
        } else return res.status(404).json({ message: `failed to load songs` });
    } catch (err) {
        return res.status(500).json({ message: 'Interval server error', err });
    }
}
export async function deleteSongFromPlaylist(req, res) {
    const { playlistId, songId } = req.body;
    const query = `DELETE FROM [dbo].[Playlist_song]
      WHERE playlist_id = @playlistId and song_id = @songId`;
    try {
        const pool = await getConnection();
        const result = await pool
            .request()
            .input('playlistId', sql.Int, playlistId)
            .input('songId', sql.Int, songId)
            .query(query);

        if (result.rowsAffected[0] > 0) {
            return res.json({
                message: 'Song removed from playlist successfully',
            });
        } else {
            return res
                .status(404)
                .json({ message: 'Song not found in playlist' });
        }
    } catch (err) {
        return res.status(500).json({ message: 'Internal server error', err });
    }
}

export async function getListSongsByPlaylistId(req, res) {
    const { playlistId } = req.body;
    const query = `SELECT ps.[song_id],s.title,s.type_id,s.image,s.file_path,s.duration,s.created_date,s.artist,s.album
  FROM [MUSTIFY].[dbo].[Playlist_song] ps 
  join Song s on ps.song_id = s.song_id
  join Playlists pl on ps.playlist_id = pl.playlist_id
  where ps.playlist_id = @playlistId`;
    try {
        const pool = await getConnection();
        const rs = await pool
            .request()
            .input('playlistId', sql.Int, playlistId)
            .query(query);
        const songs = rs.recordset.map((row) => {
            return {
                song_id: row.song_id,
                title: row.title,
                type_id: row.type_id,
                artist: row.artist,
                album: row.album,
                duration: row.duration,
                file_path: row.file_path,
                created_date: row.created_date,
                image: row.image,
            };
        });
        if (songs.length > 0) return res.json(songs);
        else return res.status(404).json({ message: 'fail to get songs' });
    } catch (err) {
        return res.status(500).json({ message: 'Interval server error', err });
    }
}

export async function getAllSongs(req, res) {
    const query = `SELECT  [song_id]
                           ,[title]
                           ,[type_id]
                           ,[artist]
                           ,[album]
                           ,[duration]
                           ,[file_path]
                           ,[created_date]
                           ,[image]
                FROM [MUSTIFY].[dbo].[Song]
                ORDER BY [created_date] DESC;`;
    try {
        const pool = await getConnection();
        const rs = await pool.request().query(query);
        const songs = rs.recordset.map((row) => {
            return {
                song_id: row.song_id,
                title: row.title,
                type_id: row.type_id,
                artist: row.artist,
                album: row.album,
                duration: row.duration,
                file_path: row.file_path,
                created_date: row.created_date,
                image: row.image,
            };
        });
        if (songs.length > 0) return res.json(songs);
        else {
            console.log('fail to get songs');
            return res.status(404).json({ message: 'fail to get songs' });
        }
    } catch (err) {
        return res
            .status(500)
            .json({ message: `Interval server error: ${err}` });
    }
}

export async function getSongLikeByUserIdAndSongId(req, res) {
    const { userId, songId } = req.body;
    const query = `SELECT [like_id]
      ,l.[user_id]
      ,l.[song_id]
      ,s.album, s.artist, s.created_date, s.duration, s.file_path, s.image, s.title, s.type_id
  FROM [MUSTIFY].[dbo].[Likes] l
  join Song s on l.song_id = s.song_id
  where l.user_id = @userId and l.song_id = @songId`;

    try {
        const pool = await getConnection();
        const rs = await pool
            .request()
            .input('userId', sql.Int, userId)
            .input('songId', sql.Int, songId)
            .query(query);
        if (rs.recordset.length > 0) {
            const song = rs.recordset[0];
            const songResponse = {
                song_id: song.song_id,
                title: song.title,
                type_id: song.type_id,
                artist: song.artist,
                album: song.album,
                duration: song.duration,
                file_path: song.file_path,
                created_date: song.created_date,
                image: song.image,
            };
            return res.json(songResponse);
        } else {
            return res.status(404).json({ message: 'song not found' });
        }
    } catch (err) {
        return res.json({
            message: `fail to get song like by user id and song id: ${err}`,
        });
    }
}
