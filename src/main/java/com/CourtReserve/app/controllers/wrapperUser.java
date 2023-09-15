package com.CourtReserve.app.controllers;

import com.azure.cosmos.implementation.User;

import java.util.Objects;

public class wrapperUser {

        private User u;

        public wrapperUser(User u) {
            this.u = u;
        }

        public User unwrap() {
            return this.u;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            wrapperUser that = (wrapperUser) o;
            return Objects.equals(u.getId(), that.u.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(u.getId());
        }
    }

