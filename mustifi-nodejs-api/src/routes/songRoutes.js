import express from 'express';
const router = express.Router();
import {
    getLastestSongs,
    getAllSongs,
    getSongLikeByUserIdAndSongId,
    getListSongsByPlaylistId,
    deleteSongFromPlaylist,
} from '../controller/songController.js';

router.get('/getLastestSongs', getLastestSongs);
router.get('/getAllSongs', getAllSongs);
router.post('/getSongLikeByUserIdAndSongId', getSongLikeByUserIdAndSongId);
router.post('/getListSongsByPlaylistId', getListSongsByPlaylistId);
router.post('/deleteSongFromPlaylist', deleteSongFromPlaylist);

export default router;
