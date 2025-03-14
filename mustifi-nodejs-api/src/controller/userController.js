import { sql, getConnection } from '../config/dbconfig.js';

// Lấy người dùng bằng username và password
export async function getExistUser(req, res) {
  const { username, password } = req.body;
  const query = `
    SELECT [user_id], [username], [password], [first_name], [last_name], [gender],
           [phone], [email], [address], [join_date], [account_type], [is_active]
    FROM [dbo].[User]
    WHERE username = @username AND password = @password;
  `;

  try {
    const pool = await getConnection();
    const result = await pool
      .request()
      .input('username', sql.VarChar, username)
      .input('password', sql.VarChar, password)
      .query(query);

    if (result.recordset.length > 0) {
      const user = result.recordset[0];
      return res.json(user);
    } else {
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
      .input('usernameOrEmail', sql.NVarChar, usernameOrEmail)
      .query(query);

    if (result.recordset.length > 0) {
      return res.json(result.recordset[0]);
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
        SELECT [User_id]
        FROM [dbo].[User]
        WHERE email = @email;
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
  const {
    username,
    password,
    first_name,
    last_name,
    gender,
    email,
    account_type,
  } = req.body;
  const query = `
        INSERT INTO [dbo].[User] 
        ([username], [password], [first_name], [last_name], [gender], [email], [account_type])
        VALUES (@username, @password, @first_name, @last_name, @gender, @email, @account_type);
    `;

  try {
    const pool = await getConnection();
    await pool
      .request()
      .input('username', sql.VarChar, username)
      .input('password', sql.VarChar, password)
      .input('first_name', sql.VarChar, first_name)
      .input('last_name', sql.VarChar, last_name)
      .input('gender', sql.Bit, gender)
      .input('email', sql.VarChar, email)
      .input('account_type', sql.Int, account_type)
      .query(query);

    return res.json({ message: 'User added successfully' });
  } catch (err) {
    console.error('Error adding user:', err);
    return res.status(500).json({ message: 'Internal Server Error' });
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

    const users = result.recordset;

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
