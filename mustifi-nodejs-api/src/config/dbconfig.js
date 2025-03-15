import sql from 'mssql';
import dotenv from 'dotenv';

dotenv.config();

const dbConfig = {
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    server: process.env.DB_SERVER,
    database: process.env.DB_DATABASE,
    options: {
        encrypt: false,
        trustServerCertificate: true,
    },
    // port: 1433, // Cấu hình port nếu cần thiết
};

// Kết nối cơ sở dữ liệu
const getConnection = async () => {
    try {
        const pool = await sql.connect(dbConfig);
        console.log('database connected!');
        return pool;
    } catch (err) {
        console.error('Database connection failed:', err);
        throw err;
    }
};

export { sql, getConnection };
