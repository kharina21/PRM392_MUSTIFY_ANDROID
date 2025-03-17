import express from 'express';
const router = express.Router();
import {
    get10SongsRecentlyPlayedByUserId,
    addSongPlayed,
} from '../controller/recentlyPlayedController.js';

router.post(
    '/get10SongsRecentlyPlayedByUserId',
    get10SongsRecentlyPlayedByUserId
);
router.post('/addSongPlayed', addSongPlayed);

export default router;
