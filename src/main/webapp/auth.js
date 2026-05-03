var LitHubAuth = (function() {
    var TOKEN_KEY    = 'lithub_token';
    var USERNAME_KEY = 'lithub_username';
    var USERID_KEY   = 'lithub_userid';

    return {
        save: function(token, username, userId) {
            sessionStorage.setItem(TOKEN_KEY,    token);
            sessionStorage.setItem(USERNAME_KEY, username);
            sessionStorage.setItem(USERID_KEY,   String(userId));
        },

        clear: function() {
            sessionStorage.removeItem(TOKEN_KEY);
            sessionStorage.removeItem(USERNAME_KEY);
            sessionStorage.removeItem(USERID_KEY);
        },

        getToken: function()    { return sessionStorage.getItem(TOKEN_KEY); },
        getUsername: function() { return sessionStorage.getItem(USERNAME_KEY); },
        getUserId: function()   { return sessionStorage.getItem(USERID_KEY); },
        isLoggedIn: function()  { return !!sessionStorage.getItem(TOKEN_KEY); },

        authHeaders: function() {
            var token = this.getToken();
            return token ? { 'Authorization': 'Bearer ' + token } : {};
        },

        logout: function() {
            fetch('/main/api/logout', {
                method: 'POST',
                headers: this.authHeaders()
            }).then(function() {
                // Do nothing on success
            }).catch(function(_) {
                // Ignore errors
            }).finally(function() {
                this.clear();
                window.location.href = 'HomePage.html';
            }.bind(this));
        }
    };
})();