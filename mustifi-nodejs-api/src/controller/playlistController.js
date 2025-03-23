import { sql, getConnection } from '../config/dbconfig.js';

export async function getAllPlaylist(req, res) {
    const { userId } = req.body;
    const query = `SELECT 
    p.playlist_id,
    p.playlist_name,
    p.user_id,
    p.created_date,
    COUNT(DISTINCT ps.song_id) AS song_count
FROM Playlists p
LEFT JOIN Playlist_song ps ON p.playlist_id = ps.playlist_id
where p.user_id = @userId
GROUP BY p.playlist_id, p.playlist_name, p.user_id, p.created_date
ORDER BY p.created_date DESC;
`;
    try {
        const pool = await getConnection();
        const rs = await pool
            .request()
            .input('userId', sql.Int, userId)
            .query(query);
        const playlists = rs.recordset.map((row) => {
            return {
                playlist_id: row.playlist_id,
                playlist_name: row.playlist_name,
                user_id: row.user_id,
                created_date: row.created_date,
                song_count: row.song_count,
            };
        });
        return res.json(playlists);
    } catch (err) {
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}
export async function addSongToPlaylist(req, res) {
    const { playlistId, songId } = req.body;
    const query = `INSERT INTO [dbo].[Playlist_song]
           ([playlist_id]
           ,[song_id])
     VALUES
           (@playlistId
           ,@songId);`;
    try {
        const pool = await getConnection();
        await pool
            .request()
            .input('playlistId', sql.Int, playlistId)
            .input('songId', sql.Int, songId)
            .query(query);
        return res.json({
            success: true,
            message: 'Add song to playlist successfully',
        });
    } catch (err) {
        return res
            .status(500)
            .json({ success: false, message: 'Internal Server Error' });
    }
}

export async function deletePlaylist(req, res) {
    const { playlistId } = req.body;
    const query = `BEGIN TRANSACTION;

-- Xóa tất cả các bản ghi trong bảng playlist_song có liên quan đến playlist_id
DELETE FROM playlist_song WHERE playlist_id = @PlaylistId;

-- Xóa playlist trong bảng playlist
DELETE FROM playlists WHERE playlist_id = @PlaylistId;

COMMIT;`;
    try {
        const pool = await getConnection();
        await pool
            .request()
            .input('playlistId', sql.Int, playlistId)
            .query(query);
        return res.json({
            success: true,
            message: 'Delete playlist successfully',
        });
    } catch (err) {
        return res
            .status(500)
            .json({ success: false, message: 'Internal Server Error' });
    }
}
export async function createPlaylist(req, res) {
    const { userId, playlistName } = req.body;
    const query = `INSERT INTO [dbo].[Playlists]
           ([playlist_name]
           ,[user_id])
     VALUES
           (@playlistName
           ,@userId)`;
    try {
        const pool = await getConnection();
        await pool
            .request()
            .input('playlistName', sql.NVarChar, playlistName)
            .input('userId', sql.Int, userId)
            .query(query);
        return res.json({
            success: true,
            message: 'Create playlist successfully',
        });
    } catch (err) {
        console.log('create playlist error: ', err);
        return res
            .status(500)
            .json({ success: false, message: 'Internal Server Error' });
    }
}

export async function deleteSongToPlaylist(req, res) {
    const { playlistId, songId } = req.body;
    const query = `DELETE FROM [dbo].[Playlist_song]
    WHERE playlist_id = @playlistId AND song_id = @songId;`;
    try {
        const pool = await getConnection();
        await pool
            .request()
            .input('playlistId', sql.Int, playlistId)
            .input('songId', sql.Int, songId)
            .query(query);
        return res.json({
            success: true,
            message: 'Delete song from playlist successfully',
        });
    } catch (err) {
        return res
            .status(500)
            .json({ success: false, message: 'Internal Server Error' });
    }
}
export async function getListPlaylistContainSong(req, res) {
    const { userId, songId } = req.body;
    const query = `SELECT 
    p.playlist_id,
    p.playlist_name,
    p.user_id,
    p.created_date,
    (SELECT COUNT(*) FROM Playlist_song WHERE playlist_id = p.playlist_id) AS song_count
FROM Playlists p
WHERE p.playlist_id IN (SELECT DISTINCT playlist_id FROM Playlist_song WHERE song_id = @songId)
AND p.user_id = @userId
ORDER BY p.created_date DESC;`;
    try {
        const pool = await getConnection();
        const rs = await pool
            .request()
            .input('userId', sql.Int, userId)
            .input('songId', sql.Int, songId)
            .query(query);
        const playlists = rs.recordset.map((row) => {
            return {
                playlist_id: row.playlist_id,
                playlist_name: row.playlist_name,
                user_id: row.user_id,
                created_date: row.created_date,
                song_count: row.song_count,
            };
        });
        return res.json(playlists);
    } catch (err) {
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}

export async function getListPlaylistNotContainSong(req, res) {
    const { userId, songId } = req.body;
    const query = `SELECT 
    p.playlist_id,
    p.playlist_name,
    p.user_id,
    p.created_date,
    COUNT(ps.song_id) AS song_count,
    'Does Not Contain Song' AS category
FROM Playlists p
LEFT JOIN Playlist_song ps ON p.playlist_id = ps.playlist_id
WHERE p.user_id = @userId 
AND p.playlist_id NOT IN (
    SELECT playlist_id FROM Playlist_song WHERE song_id = @songId
)
GROUP BY p.playlist_id, p.playlist_name, p.user_id, p.created_date

ORDER BY created_date DESC;`;
    try {
        const pool = await getConnection();
        const rs = await pool
            .request()
            .input('userId', sql.Int, userId)
            .input('songId', sql.Int, songId)
            .query(query);
        const playlists = rs.recordset.map((row) => {
            return {
                playlist_id: row.playlist_id,
                playlist_name: row.playlist_name,
                user_id: row.user_id,
                created_date: row.created_date,
                song_count: row.song_count,
            };
        });
        return res.json(playlists);
    } catch (err) {
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}
