import { sql, getConnection } from '../config/dbconfig.js';

// Lấy người dùng bằng username và password
export async function getExistUser(req, res) {
    const { usernameOrEmail, password } = req.body;
    const query = `
    SELECT [user_id], [username], [password], [first_name], [last_name], [gender],
           [phone], [email], [address], [join_date], [account_type], [is_active]
    FROM [dbo].[User]
    where (username = @usernameOrEmail or email= @usernameOrEmail) and password = @password
  `;

    try {
        const pool = await getConnection();
        const result = await pool
            .request()
            .input('usernameOrEmail', sql.VarChar, usernameOrEmail)
            .input('password', sql.VarChar, password)
            .query(query);
        if (result.recordset.length > 0) {
            const user = result.recordset[0];
            console.log('recordSet > 0');
            const userResponse = {
                id: user.user_id,
                username: user.username,
                password: user.password,
                first_name: user.first_name,
                last_name: user.last_name,
                gender: user.gender,
                phone: user.phone,
                email: user.email,
                address: user.address,
                joined_date: user.join_date,
                account_type: user.account_type,
                is_active: user.is_active,
            };
            console.log(`Found user: ${user.username}`);
            return res.json(userResponse);
        } else {
            console.log('khong tim thay user');
            return res.status(404).json({ message: 'User not found' });
        }
    } catch (err) {
        console.error('Error fetching user:', err);
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}

// Lấy người dùng theo username hoặc email
export async function getUserByUsernameOrEmail(req, res) {
    const { usernameOrEmail } = req.body;
    const query = `
        SELECT [User_id], [username], [password], [first_name], [last_name], [gender], 
               [phone], [email], [address], [join_date], [account_type], [is_active]
        FROM [dbo].[User]
        WHERE username = @usernameOrEmail OR email = @usernameOrEmail;
    `;
    try {
        const pool = await getConnection();
        const result = await pool
            .request()
            .input('usernameOrEmail', sql.VarChar, usernameOrEmail)
            .query(query);

        if (result.recordset.length > 0) {
            const user = result.recordset[0];
            const userResponse = {
                id: user.user_id,
                username: user.username,
                password: user.password,
                first_name: user.first_name,
                last_name: user.last_Name,
                gender: user.gender,
                phone: user.phone,
                email: user.email,
                address: user.address,
                joined_date: user.join_date,
                account_type: user.account_type,
                is_active: user.is_active,
            };
            return res.json(userResponse);
        } else {
            return res.status(404).json({ message: 'User not found' });
        }
    } catch (err) {
        console.error('Error fetching user:', err);
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}

// Thay đổi mật khẩu của người dùng
export async function changePasswordByUsername(req, res) {
    const { username, password } = req.body;
    const query = `
        UPDATE [dbo].[User]
        SET [password] = @password
        WHERE username = @username;
    `;

    try {
        const pool = await getConnection();
        const result = await pool
            .request()
            .input('password', sql.VarChar, password)
            .input('username', sql.VarChar, username)
            .query(query);

        return res.json({ message: 'Password updated successfully' });
    } catch (err) {
        console.error('Error updating password:', err);
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}

// Kiểm tra email đã tồn tại hay chưa
export async function checkEmailExist(req, res) {
    const { email } = req.body;
    const query = `
        SELECT *FROM [dbo].[User] WHERE email = @email;
    `;

    try {
        const pool = await getConnection();
        const result = await pool
            .request()
            .input('email', sql.VarChar, email)
            .query(query);

        if (result.recordset.length > 0) {
            return res.json({ exists: true });
        } else {
            return res.json({ exists: false });
        }
    } catch (err) {
        console.error('Error checking email:', err);
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}

// Kiểm tra username đã tồn tại hay chưa
export async function checkUsernameExist(req, res) {
    const { username } = req.body;
    const query = `
        SELECT [User_id]
        FROM [dbo].[User]
        WHERE username = @username;
    `;

    try {
        const pool = await getConnection();
        const result = await pool
            .request()
            .input('username', sql.VarChar, username)
            .query(query);

        if (result.recordset.length > 0) {
            return res.json({ exists: true });
        } else {
            return res.json({ exists: false });
        }
    } catch (err) {
        console.error('Error checking username:', err);
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}

// Thêm người dùng mới
export async function addUser(req, res) {
    const user = req.body;
    const query = `
    INSERT INTO [dbo].[User] 
    ([username], [password], [first_name], [last_name], [address], [gender], [email], [account_type], [is_active])
    VALUES (@username, @password, @first_name, @last_name, @address, @gender, @email, @account_type, @is_active);
  `;

    try {
        const pool = await getConnection();
        await pool
            .request()
            .input('username', sql.VarChar, user.username)
            .input('password', sql.VarChar, user.password)
            .input('first_name', sql.VarChar, user.first_name)
            .input('last_name', sql.VarChar, user.last_name)
            .input('address', sql.VarChar, user.address)
            .input('gender', sql.Bit, user.gender)
            .input('email', sql.VarChar, user.email)
            .input('account_type', sql.Int, user.account_type)
            .input('is_active', sql.Bit, user.is_active)
            .query(query);
        console.log('add User oklea');
        return res.json({ success: true, message: 'User added successfully' });
    } catch (err) {
        console.error('Error adding user:', err);
        return res
            .status(500)
            .json({ success: false, message: 'Internal Server Error' });
    }
}
export async function getAllUser(req, res) {
    const query = `
    SELECT 
      [User_id], [username], [password], [first_name], [last_name], [gender],
      [phone], [email], [address], [join_date], [account_type], [is_active]
    FROM [dbo].[User];
  `;

    try {
        const pool = await getConnection();
        const result = await pool.request().query(query);
        const users = result.recordset.map((row) => {
            return {
                id: row.user_id,
                username: row.username,
                password: row.password,
                first_name: row.first_name,
                last_name: row.last_Name,
                gender: row.gender,
                phone: row.phone,
                email: row.email,
                address: row.address,
                joined_date: row.join_date,
                account_type: row.account_type,
                is_active: row.is_active,
            };
        });

        if (users.length > 0) {
            return res.json(users);
        } else {
            return res.status(404).json({ message: 'No users found' });
        }
    } catch (err) {
        console.error('Error fetching users:', err);
        return res.status(500).json({ message: 'Internal Server Error' });
    }
}

export async function changeActiveStatusByUsername(req, res) {
    const { username, active } = req.body;
    const query = `update [dbo].[User]
    set [is_active] = @active where username = @username`;
    try {
        const pool = await getConnection();
        await pool
            .request()
            .input('active', sql.Bit, active)
            .input('username', sql.VarChar, username)
            .query(query);
        return res.json({
            success: true,
            message: 'change active status successed',
        });
    } catch (err) {
        console.error('change active status fail:', err);
        return res
            .status(500)
            .json({ success: false, message: 'Internal Server Error' });
    }
}
