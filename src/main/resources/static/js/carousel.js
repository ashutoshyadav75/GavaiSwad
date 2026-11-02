
document.addEventListener("DOMContentLoaded", function () {

    const carouselElement = document.getElementById("gavaiCarousel");

    if (carouselElement && typeof bootstrap !== "undefined") {

        new bootstrap.Carousel(carouselElement, {
            interval: 4000,
            ride: "carousel",
            pause: "hover",
            wrap: true,
            touch: true
        });

    }

});
