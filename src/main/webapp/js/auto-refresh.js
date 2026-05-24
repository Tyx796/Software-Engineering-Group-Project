(function () {
    const panel = document.getElementById("auto-refresh-panel");
    if (!panel) {
        return;
    }

    const intervalSeconds = Number.parseInt(panel.dataset.seconds || "", 10);
    if (!Number.isFinite(intervalSeconds) || intervalSeconds <= 0) {
        return;
    }

    const statusElement = document.getElementById("auto-refresh-status");
    const countdownElement = document.getElementById("auto-refresh-countdown");
    const refreshNowButton = document.getElementById("auto-refresh-now");
    const toggleButton = document.getElementById("auto-refresh-toggle");
    const label = panel.dataset.label || "this page";
    const dirtyForms = new Set();
    let remainingSeconds = intervalSeconds;
    let paused = false;

    function isEditableElement(element) {
        if (!element) {
            return false;
        }
        return element.matches("input, textarea, select") || element.isContentEditable;
    }

    function hasDirtyForms() {
        return Array.from(dirtyForms).some((form) => document.body.contains(form));
    }

    function shouldDelayRefresh() {
        return document.hidden || isEditableElement(document.activeElement) || hasDirtyForms();
    }

    function updatePanel(message) {
        if (statusElement) {
            statusElement.textContent = label;
        }
        if (!countdownElement) {
            return;
        }
        if (message) {
            countdownElement.textContent = message;
            return;
        }
        countdownElement.textContent = "Refreshes automatically in " + remainingSeconds + " seconds.";
    }

    function reloadPage() {
        window.location.reload();
    }

    function tick() {
        if (paused) {
            updatePanel("Auto-refresh paused.");
            return;
        }
        if (shouldDelayRefresh()) {
            remainingSeconds = intervalSeconds;
            updatePanel("Waiting until you stop interacting before refreshing.");
            return;
        }
        remainingSeconds -= 1;
        if (remainingSeconds <= 0) {
            reloadPage();
            return;
        }
        updatePanel();
    }

    document.querySelectorAll("form").forEach((form) => {
        form.addEventListener("input", function () {
            dirtyForms.add(form);
        });
        form.addEventListener("change", function () {
            dirtyForms.add(form);
        });
        form.addEventListener("submit", function () {
            dirtyForms.delete(form);
        });
    });

    refreshNowButton?.addEventListener("click", reloadPage);
    toggleButton?.addEventListener("click", function () {
        paused = !paused;
        remainingSeconds = intervalSeconds;
        toggleButton.textContent = paused ? "Resume" : "Pause";
        updatePanel(paused ? "Auto-refresh paused." : null);
    });

    document.addEventListener("visibilitychange", function () {
        if (!document.hidden && !paused) {
            remainingSeconds = intervalSeconds;
            updatePanel();
        }
    });

    updatePanel();
    window.setInterval(tick, 1000);
})();
