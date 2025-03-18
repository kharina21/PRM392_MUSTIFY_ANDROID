import express from 'express';
const router = express.Router();
import { getLastestSongs, getAllSongs } from '../controller/songController.js';

router.get('/getLastestSongs', getLastestSongs);
router.get('/getAllSongs', getAllSongs);

export default router;
