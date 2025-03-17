import express from 'express';
import {
    removeSongInListLike,
    getListSongLikeByUserId,
} from '../controller/likeController.js';
const router = express.Router();

router.post('/removeSongInListLike', removeSongInListLike);
router.post('/getListSongLikeByUserId', getListSongLikeByUserId);

export default router;
