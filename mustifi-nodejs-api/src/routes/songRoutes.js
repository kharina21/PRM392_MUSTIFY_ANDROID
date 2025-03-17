import express from 'express';
const router = express.Router();
import { getLastest5Songs, getAllSongs } from '../controller/songController.js';

router.get('/getLastest5Songs', getLastest5Songs);
router.get('/getAllSongs', getAllSongs);

export default router;
