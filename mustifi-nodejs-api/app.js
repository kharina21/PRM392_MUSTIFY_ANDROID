import express from 'express';
import bodyParser from 'body-parser'; // Import body-parser qua default import
import userRoutes from './src/routes/userRoutes.js'; // Import các route bằng ES module
import cors from 'cors';

const app = express();
const port = 3505;

app.use(cors());
app.use(express.urlencoded({ extended: true })); // Hỗ trợ form-data
// Middleware
app.use(bodyParser.json());

// Sử dụng các route đã định nghĩa
app.use('/api/users', userRoutes);

app.listen(port, () => {
    console.log(`Server running on port ${port}`);
});
