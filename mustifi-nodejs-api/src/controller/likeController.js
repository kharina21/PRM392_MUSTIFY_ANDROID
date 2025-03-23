import { sql, getConnection } from '../config/dbconfig.js';

export async function deleteSongInListLike(req, res) {
    const { userId, songId } = req.body;
    const query = `DELETE FROM [dbo].[Likes] 
                   WHERE user_id = @userId and song_id = @songId`;

    try {
        const pool = await getConnection();
        await pool
            .request()
            .input('userId', sql.Int, userId)
            .input('songId', sql.Int, songId)
            .query(query);
        console.log('remove song in list song like successed');
        return res.json({
            success: true,
            message: 'remove song in list song like successed',
        });
    } catch (err) {
        return res.status(500).json({
            success: false,
            message: 'remove song to list song like false',
            err,
        });
    }
}

export async function getListSongLikeByUserId(req, res) {
    const { userId } = req.body;
    const query = `SELECT L.like_id, L.user_id, L.song_id, L.created_date as date_add, 
   S.title, S.album, S.artist, S.created_date,S.duration,S.file_path,S.image,S.type_id,
  U.account_type, U.address, U.email,U.first_name,U.gender,U.is_active,U.join_date,U.last_name,U.password,U.phone,U.username
                FROM Likes L
				JOIN [User] U On L.user_id = U.user_id
                JOIN Song S ON L.song_id = S.song_id
                WHERE L.user_id = @userId
                ORDER BY date_add DESC;`;
    try {
        const pool = await getConnection();
        const rs = await pool
            .request()
            .input('userId', sql.Int, userId)
            .query(query);
        const songs = rs.recordset.map((row) => {
            return {
                like_id: row.like_id,
                user: {
                    id: row.user_id,
                    username: row.username,
                    password: row.password,
                    first_name: row.first_name,
                    last_name: row.last_name,
                    gender: row.gender,
                    phone: row.phone,
                    address: row.address,
                    email: row.email,
                    joined_date: row.join_date,
                    account_type: row.accont_type,
                    is_active: row.is_active,
                },
                song: {
                    song_id: row.song_id,
                    title: row.title,
                    type_id: row.type_id,
                    artist: row.artist,
                    album: row.album,
                    duration: row.duration,
                    file_path: row.file_path,
                    created_date: row.date_add,
                    image: row.image,
                },
                created_date: row.created_date,
            };
        });
        if (songs.length > 0) {
            return res.json(songs);
        } else
            return res.status(404).json({ message: 'interval server error!' });
    } catch (err) {
        return res
            .status(500)
            .json({ message: `fail to get list like song: ${err}` });
    }
}

export async function addSongToListLike(req, res) {
    const { userId, songId } = req.body;
    const query = `INSERT INTO [dbo].[Likes]
                          ([user_id]
                           ,[song_id])
                     VALUES(@userId,@songId)`;
    try {
        const pool = await getConnection();
        await pool
            .request()
            .input('userId', sql.Int, userId)
            .input('songId', sql.Int, songId)
            .query(query);
        console.log('add song success');
        return res.json({
            success: true,
            message: 'add song to like list successed!',
        });
    } catch (err) {
        return res
            .status(500)
            .json({ success: false, message: 'Interval server err', err });
    }
}
