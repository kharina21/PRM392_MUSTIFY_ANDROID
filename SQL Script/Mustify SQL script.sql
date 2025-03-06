create database MUSTIFY
GO
use MUSTIFY
GO

create table User_type(
type_id int primary key identity,
type_name varchar(30) not null,
description nvarchar(300),
)

create table Users(
user_id int primary key identity,
username varchar(20) not null,
password varchar(20) not null,
first_name nvarchar(30) not null,
last_name nvarchar(30) not null,
gender bit default 0 not null,
phone varchar(13),
email varchar(30),
address nvarchar(150) not null,
join_date datetime default getdate(),
account_type int foreign key references User_type(type_id),
is_active bit default 0,
)

create table Song_types(
id int primary key identity,
type_name nvarchar(255),
description nvarchar(255),
)

create table Songs(
song_id int primary key identity,
title nvarchar(255) not null,
type_id int foreign key references Song_types(id),
artist nvarchar(255) not null,
album nvarchar(255),
duration int not null,
file_path varchar(MAX) not null,
created_date datetime default getdate(),
)

alter table Songs
add image varchar(max)

create table Playlists(
playlist_id int primary key identity,
playlist_name nvarchar(255),
user_id int foreign key references Users(user_id),
created_date datetime default getdate(),
)

create table Playlist_songs(
id int primary key identity,
playlist_id int foreign key references Playlists(playlist_id),
song_id int foreign key references Songs(song_id),
)

create table Likes(
like_id int primary key identity,
user_id int foreign key references Users(user_id),
song_id int foreign key references Songs(song_id),
created_date datetime default getdate(),
)

create table Recently_played(
id int primary key identity,
user_id int foreign key references Users(user_id),
song_id int foreign key references Songs(song_id),
played_at datetime default getdate(),
)


USE [MUSTIFY]
GO

USE [MUSTIFY]
GO

INSERT INTO [dbo].[User_type] ([type_name],[description])
     VALUES ('admin','admin')
GO
INSERT INTO [dbo].[User_type] ([type_name], [description])
     VALUES('user' ,'user')
GO


INSERT INTO [dbo].[Users] ([username],[password],[first_name],[last_name],[gender],[phone],[email],[address],[account_type],[is_active])
     VALUES ('admin','123','Nguyen','Admin',1,'0327983593','kharrr2001@gmail.com','Ha Noi',1,1)
GO



/*
drop table User_type
drop table Users
drop table Song_types
drop table Songs
drop table Playlists
drop table Playlist_songs
drop table Likes
drop table Recently_played
*/


