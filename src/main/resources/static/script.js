document.getElementById("loginForm").addEventListener("submit", function(event) {

    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    fetch("/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email,
            password: password
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Invalid email or password");
        }
        return response.json();
    })
    .then(data => {

        if (data.role === "USER") {
            window.location.href = "user.html";
        } 
        else if (data.role === "ADMIN") {
            window.location.href = "admin.html";
        }

    })
    .catch(error => {
        document.getElementById("message").textContent = error.message;
    });

});