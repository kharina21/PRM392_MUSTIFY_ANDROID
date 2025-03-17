import { sql, getConnection } from '../config/dbconfig.js';

export async function getLastest5Songs(req, res) {
    const query = `SELECT TOP 5 [song_id]
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
