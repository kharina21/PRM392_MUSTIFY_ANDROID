import express from 'express';
import {
    deleteSongInListLike,
    getListSongLikeByUserId,
    addSongToListLike,
} from '../controller/likeController.js';
const router = express.Router();

router.post('/deleteSongInListLike', deleteSongInListLike);
router.post('/getListSongLikeByUserId', getListSongLikeByUserId);
router.post('/addSongToListLike', addSongToListLike);

export default router;
