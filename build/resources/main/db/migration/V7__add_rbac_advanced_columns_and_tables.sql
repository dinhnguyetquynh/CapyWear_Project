
CREATE TABLE permissions (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL UNIQUE,
                             description TEXT,
                             resource VARCHAR(255),
                             action VARCHAR(255)
);
CREATE TABLE role_permission (
                                 role_id BIGINT NOT NULL,
                                 permission_id BIGINT NOT NULL,

                                 PRIMARY KEY (role_id, permission_id),

                                 CONSTRAINT fk_role_permission_role
                                     FOREIGN KEY (role_id)
                                         REFERENCES roles(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_role_permission_permission
                                     FOREIGN KEY (permission_id)
                                         REFERENCES permissions(id)
                                         ON DELETE CASCADE
);

CREATE TABLE user_permissions (
                                  user_id BIGINT NOT NULL,
                                  permission_id BIGINT NOT NULL,
                                  PRIMARY KEY (user_id, permission_id),
                                  CONSTRAINT fk_user_permissions_user
                                      FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                                  CONSTRAINT fk_user_permissions_permission
                                      FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);