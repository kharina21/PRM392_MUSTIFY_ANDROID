import express from 'express';
const router = express.Router();
import {
  getExistUser,
  checkEmailExist,
  checkUsernameExist,
  addUser,
  changePasswordByUsername,
  getUserByUsernameOrEmail,
  getAllUser,
} from '../controller/userController.js';

// Route cho các API
router.post('/getExistUser', getExistUser);
router.post('/checkEmailExist', checkEmailExist);
router.post('/checkUsernameExist', checkUsernameExist);
router.post('/addUser', addUser);
router.post('/changePasswordByUsername', changePasswordByUsername);
router.post('/getUserByUsernameOrEmail', getUserByUsernameOrEmail);
router.get('/getAllUser', getAllUser);

export default router;
