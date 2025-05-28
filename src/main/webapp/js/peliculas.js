// src/main/webapp/js/peliculas.js

const controllerUrl = '/TestControllerPostgre/Controller';
let currentLoggedInUser = null;
let currentMovieId = null;

function showMessage(msg, type = 'info') {
    const messageBox = document.getElementById('message');
    messageBox.textContent = msg;
    messageBox.className = 'message-box';
    if (type === 'error') {
        messageBox.classList.add('error');
    } else if (type === 'success') {
        messageBox.classList.add('success');
    }
    messageBox.classList.remove('hidden');
}

async function loadGenres() {
    try {
        const response = await fetch(`${controllerUrl}?ACTION=PELICULA.FINDALLGENEROS`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const genres = await response.json();
        const genreFilterSelect = document.getElementById('genreFilter');
        genres.forEach(genre => {
            const option = document.createElement('option');
            option.value = genre;
            option.textContent = genre;
            genreFilterSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error cargando géneros:', error);
        showMessage('Error al cargar los géneros.', 'error');
    }
}

async function loadMovies(filterType = 'all', value = 'TODOS') {
    const moviesListDiv = document.getElementById('moviesList');
    moviesListDiv.innerHTML = '';
    showMessage('', 'hidden');

    let url = `${controllerUrl}?ACTION=PELICULA.FINDALL`;
    if (filterType === 'genre' && value !== 'TODOS') {
        url += `&GENERO=${encodeURIComponent(value)}`;
    } else if (filterType === 'title' && value) {
        url += `&TITULO=${encodeURIComponent(value)}`;
    }

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const movies = await response.json();

        if (movies && movies.length > 0) {
            movies.forEach(movie => {
                const movieCard = `
                    <div class="movie-card" data-movie-id="${movie.id}" data-movie-title="${movie.titulo}">
                        <h3>${movie.titulo}</h3>
                        <p>Género: ${movie.genero}</p>
                        <p>ID: ${movie.id}</p>
                    </div>
                `;
                moviesListDiv.insertAdjacentHTML('beforeend', movieCard);
            });
            document.querySelectorAll('.movie-card').forEach(card => {
                card.addEventListener('click', function() {
                    const movieId = this.dataset.movieId;
                    const movieTitle = this.dataset.movieTitle;
                    openCommentsModal(movieId, movieTitle);
                });
            });
        } else {
            showMessage('No se encontraron películas para el criterio de búsqueda.', 'info');
        }
    } catch (error) {
        console.error('Error cargando películas:', error);
        showMessage('Error al cargar las películas.', 'error');
    }
}

const movieSearchInput = document.getElementById('movieSearchInput');
const movieSuggestionsDatalist = document.getElementById('movieSuggestions');
const searchButton = document.getElementById('searchButton');
let debounceTimeout;

async function fetchTitlesForAutocomplete(query) {
    if (query.length < 2) {
        movieSuggestionsDatalist.innerHTML = '';
        return;
    }
    try {
        const url = `${controllerUrl}?ACTION=PELICULA.FINDTITLES&QUERY=${encodeURIComponent(query)}`;
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const titles = await response.json();
        movieSuggestionsDatalist.innerHTML = '';
        if (titles && titles.length > 0) {
            titles.forEach(title => {
                const option = document.createElement('option');
                option.value = title;
                movieSuggestionsDatalist.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error fetching autocomplete titles:', error);
    }
}

movieSearchInput.addEventListener('input', (event) => {
    clearTimeout(debounceTimeout);
    const query = event.target.value;
    debounceTimeout = setTimeout(() => {
        fetchTitlesForAutocomplete(query);
    }, 300);
});

searchButton.addEventListener('click', () => {
    const searchText = movieSearchInput.value.trim();
    document.getElementById('genreFilter').value = 'TODOS';
    loadMovies('title', searchText);
});

movieSearchInput.addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        event.preventDefault();
        searchButton.click();
    }
});

// Lógica de Comentarios (Modal)
const commentsModal = document.getElementById('commentsModal');
const closeButton = commentsModal.querySelector('.close-button');
const modalMovieTitle = document.getElementById('modalMovieTitle');
const modalMovieId = document.getElementById('modalMovieId');
const commentsListDiv = document.getElementById('commentsList');
const noCommentsMessage = document.getElementById('noCommentsMessage');
const commentTextarea = document.getElementById('commentText');
const submitCommentButton = document.getElementById('submitCommentButton');
const commentMessageBox = document.getElementById('commentMessage');
const loginPrompt = document.getElementById('loginPrompt');

async function openCommentsModal(movieId, movieTitle) {
    currentMovieId = movieId;
    modalMovieTitle.textContent = movieTitle;
    modalMovieId.textContent = movieId;
    commentsModal.classList.remove('hidden');
    await loadCommentsForMovie(movieId);

    if (currentLoggedInUser) {
        document.getElementById('addCommentSection').classList.remove('hidden');
        loginPrompt.classList.add('hidden');
    } else {
        document.getElementById('addCommentSection').classList.add('hidden');
        loginPrompt.classList.remove('hidden');
    }
    commentTextarea.value = '';
    commentMessageBox.classList.add('hidden');
}

closeButton.addEventListener('click', () => {
    commentsModal.classList.add('hidden');
    currentMovieId = null;
});

window.addEventListener('click', (event) => {
    if (event.target === commentsModal) {
        commentsModal.classList.add('hidden');
        currentMovieId = null;
    }
});

function showMessageModal(msg, type = 'info') {
    commentMessageBox.textContent = msg;
    commentMessageBox.className = 'message-box mt-2';
    if (type === 'error') {
        commentMessageBox.classList.add('error');
    } else if (type === 'success') {
        commentMessageBox.classList.add('success');
    }
    commentMessageBox.classList.remove('hidden');
}

async function loadCommentsForMovie(movieId) {
    commentsListDiv.innerHTML = '';
    noCommentsMessage.classList.add('hidden');

    try {
        const url = `${controllerUrl}?ACTION=COMENTARIO.FINDALLBYPELICULA&PELICULA_ID=${encodeURIComponent(movieId)}`;
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();

        if (data && data.length > 0) {
            data.forEach(comment => {
                const commentDate = new Date(comment.fechaCreacion).toLocaleString();
                const commentHtml = `
                    <div class="comment-item">
                        <p>${comment.texto}</p>
                        <div class="comment-meta">
                            <span>Por: ${comment.usuarioUsername}</span>
                            <span>${commentDate}</span>
                        </div>
                    </div>
                `;
                commentsListDiv.insertAdjacentHTML('beforeend', commentHtml);
            });
        } else {
            noCommentsMessage.classList.remove('hidden');
        }
    } catch (error) {
        console.error('Error cargando comentarios:', error);
        noCommentsMessage.classList.remove('hidden');
        showMessageModal('Error al cargar los comentarios.', 'error');
    }
}

submitCommentButton.addEventListener('click', async () => {
    const commentText = commentTextarea.value.trim();
    if (!commentText) {
        showMessageModal('El comentario no puede estar vacío.', 'error');
        return;
    }

    if (!currentLoggedInUser) {
        showMessageModal('Debes iniciar sesión para comentar.', 'error');
        return;
    }

    try {
        const url = `${controllerUrl}?ACTION=COMENTARIO.ADD&PELICULA_ID=${encodeURIComponent(currentMovieId)}&TEXTO=${encodeURIComponent(commentText)}`;
        const response = await fetch(url);
        const result = await response.json();

        if (result.success) {
            showMessageModal('Comentario publicado con éxito.', 'success');
            commentTextarea.value = '';
            await loadCommentsForMovie(currentMovieId);
        } else {
            showMessageModal(`Error al publicar comentario: ${result.message}`, 'error');
        }
    } catch (error) {
        console.error('Error al enviar comentario:', error);
        showMessageModal('Error de conexión al intentar publicar el comentario.', 'error');
    }
});

document.addEventListener('DOMContentLoaded', () => {
    currentLoggedInUser = localStorage.getItem('loggedInUser');
    loadGenres();
    loadMovies();
});

document.getElementById('filterButton').addEventListener('click', () => {
    const selectedGenre = document.getElementById('genreFilter').value;
    movieSearchInput.value = '';
    loadMovies('genre', selectedGenre);
});