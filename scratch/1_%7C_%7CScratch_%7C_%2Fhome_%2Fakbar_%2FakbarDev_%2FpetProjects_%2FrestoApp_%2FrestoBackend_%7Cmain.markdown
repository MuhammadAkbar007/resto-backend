select users.id, users.email, users.visible, users.status, roles.role_type from users
	join users_roles on users.id = users_roles.user_id
	join roles on roles.id = users_roles.role_id 
	order by email;

-- SELECT * from refresh_token;
-- SELECT id, photo_id, email, phone_number, first_name from users;
-- SELECT id, compressed_id, file_path, original_name  from attachment
-- SELECT * from users_roles;
-- SELECT * from otp_verification;
-- SELECT * from attachment;
-- SELECT * from roles;

-- DELETE from users;
-- DELETE from users_roles;
-- DELETE from otp_verification;
-- DELETE from refresh_token;

-- UPDATE users set visible = TRUE 
-- 	WHERE id = '3e673bdd-a078-4bc7-a427-09963fed471c';
