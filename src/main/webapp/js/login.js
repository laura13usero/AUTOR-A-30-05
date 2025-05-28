// src/main/webapp/js/login.js

const loginForm = document.getElementById('loginForm');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const messageBox = document.getElementById('message');

loginForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();

    if (!username || !password) {
        messageBox.textContent = 'Por favor, introduce usuario y contraseña.';
        messageBox.className = 'message-box error';
        messageBox.classList.remove('hidden');
        return;
    }

    const url = `/TestControllerPostgre/Controller?ACTION=USUARIO.LOGIN&USER=${encodeURIComponent(username)}&PASSWORD=${encodeURIComponent(password)}`;

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log(data);

        if (data && data.success) {
            messageBox.textContent = `¡Login exitoso! Bienvenido, ${data.User}. Redirigiendo...`;
            messageBox.className = 'message-box success';
            localStorage.setItem('loggedInUser', data.User);
            setTimeout(() => {
                window.location.href = '/TestControllerPostgre/peliculas.html';
            }, 1500);
        } else {
            messageBox.textContent = data.message || 'Usuario o contraseña incorrectos.';
            messageBox.className = 'message-box error';
            localStorage.removeItem('loggedInUser');
        }
    } catch (error) {
        console.error('Error durante el login:', error);
        messageBox.textContent = `Error al conectar con el servidor: ${error.message}.`;
        messageBox.className = 'message-box error';
    } finally {
        messageBox.classList.remove('hidden');
    }
});