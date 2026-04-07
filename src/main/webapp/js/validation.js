/**
 * Common form validation utilities for TA Recruitment System.
 */
(function () {
    'use strict';

    /**
     * Initialise Bootstrap-style validation on all forms with class 'needs-validation'.
     */
    function initFormValidation() {
        var forms = document.querySelectorAll('form.needs-validation');
        forms.forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            });
        });
    }

    /**
     * Initialise password confirmation matching on register forms.
     */
    function initPasswordConfirmation() {
        var password = document.getElementById('password');
        var confirmPassword = document.getElementById('confirmPassword');
        if (!password || !confirmPassword) {
            return;
        }
        var form = confirmPassword.closest('form');
        if (form) {
            form.addEventListener('submit', function () {
                if (confirmPassword.value !== password.value) {
                    confirmPassword.setCustomValidity('Passwords do not match');
                } else {
                    confirmPassword.setCustomValidity('');
                }
            });
        }
        confirmPassword.addEventListener('input', function () {
            confirmPassword.setCustomValidity('');
        });
    }

    /**
     * Initialise file type validation on file inputs.
     */
    function initFileTypeValidation() {
        var fileInputs = document.querySelectorAll('input[type="file"][accept]');
        fileInputs.forEach(function (input) {
            input.addEventListener('change', function () {
                var acceptedTypes = input.getAttribute('accept').split(',').map(function (t) {
                    return t.trim().toLowerCase();
                });
                if (input.files.length > 0) {
                    var fileName = input.files[0].name.toLowerCase();
                    var valid = acceptedTypes.some(function (ext) {
                        return fileName.endsWith(ext);
                    });
                    if (!valid) {
                        input.setCustomValidity('Please select a file with an accepted format: ' + acceptedTypes.join(', '));
                    } else {
                        input.setCustomValidity('');
                    }
                }
            });
        });
    }

    // Run on DOM ready
    document.addEventListener('DOMContentLoaded', function () {
        initFormValidation();
        initPasswordConfirmation();
        initFileTypeValidation();
    });
})();
