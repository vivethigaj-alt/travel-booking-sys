document.addEventListener("DOMContentLoaded", function () {
    var exploreButton = document.getElementById("explore-cars");
    var fleetSection = document.getElementById("fleet");
    if (exploreButton && fleetSection) {
        exploreButton.addEventListener("click", function () {
            fleetSection.hidden = false;
            exploreButton.setAttribute("aria-expanded", "true");
            fleetSection.scrollIntoView({ behavior: "smooth", block: "start" });
        });
    }

    var startDate = document.querySelector('input[name="startDate"]');
    var endDate = document.querySelector('input[name="dropDate"]');
    var tripDays = document.querySelector('input[name="tripDays"]');

    function refreshTripDays() {
        if (!startDate || !endDate || !tripDays) {
            return;
        }
        if (!startDate.value || !endDate.value) {
            tripDays.value = "Choose travel dates";
            return;
        }
        var start = startDate.value.split("-").map(Number);
        var end = endDate.value.split("-").map(Number);
        var startUtc = Date.UTC(start[0], start[1] - 1, start[2]);
        var endUtc = Date.UTC(end[0], end[1] - 1, end[2]);
        var days = Math.floor((endUtc - startUtc) / 86400000) + 1;
        tripDays.value = days > 0 ? days + (days === 1 ? " day" : " days") : "Choose a valid drop date";
    }

    if (startDate && endDate) {
        var now = new Date();
        var today = [
            now.getFullYear(),
            String(now.getMonth() + 1).padStart(2, "0"),
            String(now.getDate()).padStart(2, "0")
        ].join("-");
        startDate.min = today;
        endDate.min = today;
        startDate.addEventListener("change", function () {
            endDate.min = startDate.value || endDate.min;
            if (endDate.value && endDate.value < startDate.value) {
                endDate.value = "";
            }
            refreshTripDays();
        });
        endDate.addEventListener("change", refreshTripDays);
    }

    var enquiryForm = document.getElementById("trip-enquiry-form");
    var review = document.getElementById("enquiry-review");
    var summary = document.getElementById("enquiry-summary");
    var editButton = document.getElementById("edit-enquiry");
    var vehicle = document.getElementById("vehicle");
    if (!enquiryForm || !review || !summary) {
        return;
    }

    var requestedVehicle = new URLSearchParams(window.location.search).get("car");
    if (vehicle && requestedVehicle) {
        Array.from(vehicle.options).forEach(function (option) {
            if (option.value === requestedVehicle || option.textContent === requestedVehicle) {
                vehicle.value = option.value || option.textContent;
            }
        });
    }

    function formatDate(value) {
        var parts = value.split("-").map(Number);
        return new Date(parts[0], parts[1] - 1, parts[2]).toLocaleDateString(undefined, {
            year: "numeric",
            month: "long",
            day: "numeric"
        });
    }

    enquiryForm.addEventListener("submit", function (event) {
        event.preventDefault();
        refreshTripDays();
        if (!enquiryForm.reportValidity()) {
            return;
        }

        var values = new FormData(enquiryForm);
        var rows = [
            ["Vehicle", values.get("vehicle")],
            ["Name", values.get("customerName")],
            ["Phone number", values.get("phone")],
            ["Pickup date", formatDate(values.get("startDate"))],
            ["Drop date", formatDate(values.get("dropDate"))],
            ["Number of days", tripDays.value],
            ["Pickup location", values.get("pickup")],
            ["Destination", values.get("destination")],
            ["Passengers", values.get("passengers")]
        ];
        summary.replaceChildren();
        rows.forEach(function (row) {
            var term = document.createElement("dt");
            var detail = document.createElement("dd");
            term.textContent = row[0];
            detail.textContent = row[1];
            summary.append(term, detail);
        });
        review.hidden = false;
        review.scrollIntoView({ behavior: "smooth", block: "start" });
    });

    if (editButton) {
        editButton.addEventListener("click", function () {
            review.hidden = true;
            enquiryForm.scrollIntoView({ behavior: "smooth", block: "start" });
        });
    }
});
