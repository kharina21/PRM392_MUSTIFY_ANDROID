import express from 'express';
import {
    getListPlaylistContainSong,
    getListPlaylistNotContainSong,
    addSongToPlaylist,
    deleteSongToPlaylist,
    createPlaylist,
    deletePlaylist,
    getAllPlaylist,
} from '../controller/playlistController.js';
const router = express.Router();

router.post('/getListPlaylistContainSong', getListPlaylistContainSong);
router.post('/getListPlaylistNotContainSong', getListPlaylistNotContainSong);
router.post('/addSongToPlaylist', addSongToPlaylist);
router.post('/deleteSongToPlaylist', deleteSongToPlaylist);
router.post('/createPlaylist', createPlaylist);
router.post('/deletePlaylist', deletePlaylist);
router.post('/getAllPlaylist', getAllPlaylist);

export default router;
