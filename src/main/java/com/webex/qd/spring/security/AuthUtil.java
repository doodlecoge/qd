package com.webex.qd.spring.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.InetOrgPerson;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 5:04 PM
 */
public class AuthUtil {

    private AuthUtil() {

    }

    public static String getUsername() {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
                return null;
            }
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return null;
        }
    }

    public static InetOrgPerson getPrincipal() {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
                return null;
            }
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof InetOrgPerson) {
                return (InetOrgPerson) principal;
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    public static String getFullName() {
        try {
            return ((InetOrgPerson) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getDisplayName();
        } catch (Exception e) {
            return null;
        }
    }
}
