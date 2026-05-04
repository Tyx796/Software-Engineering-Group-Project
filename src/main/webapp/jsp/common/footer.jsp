</main>
<c:if test="${not empty pageAutoRefreshSeconds}">
    <div
            id="auto-refresh-panel"
            class="auto-refresh-panel shadow-sm"
            data-seconds="${pageAutoRefreshSeconds}"
            data-label="${empty pageAutoRefreshLabel ? pageTitle : pageAutoRefreshLabel}">
        <div class="small text-uppercase text-muted">Live Refresh</div>
        <div class="fw-semibold" id="auto-refresh-status">
                ${empty pageAutoRefreshLabel ? pageTitle : pageAutoRefreshLabel}
        </div>
        <div class="small text-muted mb-3" id="auto-refresh-countdown">
            Refreshes automatically every ${pageAutoRefreshSeconds} seconds.
        </div>
        <div class="d-flex gap-2">
            <button class="btn btn-sm btn-primary" type="button" id="auto-refresh-now">Refresh now</button>
            <button class="btn btn-sm btn-outline-secondary" type="button" id="auto-refresh-toggle">Pause</button>
        </div>
    </div>
</c:if>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<script src="${pageContext.request.contextPath}/js/auto-refresh.js"></script>
</body>
</html>
