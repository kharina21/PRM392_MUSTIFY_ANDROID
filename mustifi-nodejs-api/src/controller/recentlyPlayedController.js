import { sql, getConnection } from '../config/dbconfig.js';

export async function get10SongsRecentlyPlayedByUserId(req, res) {
    const { userId } = req.body;
    const query = `SELECT TOP 10 
                       rp.[id],  
                       rp.[user_id],
                       rp.[song_id], 
                       rp.[played_at],
					   u.[username],
					   u.first_name,
					   u.last_name,
					   u.[password],
					   u.address,
					   u.email,
					   u.gender,
					   u.phone,
					   u.account_type,
					   u.join_date,
					   u.is_active,
					   s.album,
					   s.artist,
					   s.created_date,
					   s.duration,
					   s.file_path,
					   s.image,
					   s.title,
					   s.type_id
                FROM [dbo].[Recently_played] rp
                JOIN [dbo].[User] u ON rp.[user_id] = u.[user_id]
                JOIN [dbo].[Song] s ON rp.[song_id] = s.[song_id]
                WHERE rp.[user_id] = @userId
                AND rp.[played_at] = (
                    SELECT MAX(played_at)
                   FROM [dbo].[Recently_played] rp2
                   WHERE rp2.song_id = rp.song_id
                )
                ORDER BY rp.[played_at] DESC`;

    try {
        const pool = await getConnection();
        const result = await pool
            .request()
            .input('userId', sql.Int, userId)
            .query(query);
        const songs = result.recordset.map((row) => {
            return {
                id: row.id,
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
                    created_date: row.created_date,
                    image: row.image,
                },
                played_at: row.played_at,
            };
        });
        if (songs.length > 0) {
            return res.json(songs);
        } else
            return res
                .status(404)
                .json({ message: 'no recently played found!' });
    } catch (err) {
        console.log('error to get recently played: ', err);
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}

export async function addSongPlayed(req, res) {
    const { userId, songId } = req.body;
    const query =
        'INSERT INTO [dbo].[Recently_played]\n' +
        '           ([user_id]\n' +
        '           ,[song_id])\n' +
        '     VALUES\n' +
        '           (@userId,@songId)';
    try {
        const pool = await getConnection();
        await pool
            .request()
            .input('userId', sql.Int, userId)
            .input('songId', sql.Int, songId)
            .query(query);
        console.log('add song played okela');
        return res.json({
            success: true,
            message: 'add song played successed!',
        });
    } catch (err) {
        console.log('fail to add song played!', err);
        return res
            .status(500)
            .json({ success: false, message: 'fail to add song played!' });
    }
}
