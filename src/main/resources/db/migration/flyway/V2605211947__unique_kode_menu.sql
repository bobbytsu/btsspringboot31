ALTER TABLE projectz.MstMenu
    ALTER COLUMN KodeMenu varchar(3) NOT NULL
GO

ALTER TABLE projectz.MstMenu
    ADD CONSTRAINT uc_mstmenu_kodemenu UNIQUE (KodeMenu)
GO