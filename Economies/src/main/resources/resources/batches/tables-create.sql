#
#
#

CREATE TABLE IF NOT EXISTS accounts (
    id MEDIUMINT UNSIGNED AUTO_INCREMENT,
    uuid_most BIGINT NOT NULL,
    uuid_least BIGINT NOT NULL,
    identifier VARCHAR(16) NOT NULL,
    balance DECIMAL(20,2),

    CONSTRAINT uuid UNIQUE KEY (uuid_most, uuid_least),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS transactions (
    sender MEDIUMINT UNSIGNED,
    receiver MEDIUMINT UNSIGNED,
    timestamp TIMESTAMP NOT NULL,
    amount DECIMAL(20,2) NOT NULL CHECK (amount > 0),

    FOREIGN KEY (sender) REFERENCES accounts(id),
    FOREIGN KEY (receiver) REFERENCES accounts(id)
);

CREATE TABLE IF NOT EXISTS versions (
    plugin_name VARCHAR(16) NOT NULL,
    version_major TINYINT UNSIGNED NOT NULL,
    version_minor TINYINT UNSIGNED NOT NULL,

    PRIMARY KEY (plugin_name)
);


