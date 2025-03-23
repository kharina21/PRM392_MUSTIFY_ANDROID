import express from 'express';
const router = express.Router();
import {
    getLastestSongs,
    getAllSongs,
    getSongLikeByUserIdAndSongId,
} from '../controller/songController.js';

router.get('/getLastestSongs', getLastestSongs);
router.get('/getAllSongs', getAllSongs);
router.post('/getSongLikeByUserIdAndSongId', getSongLikeByUserIdAndSongId);

export default router;
