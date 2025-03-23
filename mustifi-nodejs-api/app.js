import express from 'express';
import bodyParser from 'body-parser'; // Import body-parser qua default import
import userRoutes from './src/routes/userRoutes.js'; // Import các route bằng ES module
import recentlyPlayedRoutes from './src/routes/recentlyPlayedRoutes.js';
import songRoutes from './src/routes/songRoutes.js';
import likeRoutes from './src/routes/likeRoutes.js';
import playlistRoutes from './src/routes/playlistRoutes.js';
import cors from 'cors';

const app = express();
const port = 3505;

app.use(cors());
app.use(express.urlencoded({ extended: true })); // Hỗ trợ form-data
// Middleware
app.use(bodyParser.json());

// Sử dụng các route đã định nghĩa
app.use('/api/users', userRoutes);
app.use('/api/recentlyPlayed', recentlyPlayedRoutes);
app.use('/api/songs', songRoutes);
app.use('/api/like', likeRoutes);
app.use('/api/playlist/', playlistRoutes);

app.listen(port, () => {
    console.log(`Server running on port ${port}`);
});
