import { firebaseConfig } from '/config.js';
const firebaseApp = firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();
const firestore = firebase.firestore();

const signupForm = document.querySelector('.registration.form');
const loginForm = document.querySelector('.login.form');
const forgotForm = document.querySelector('.forgot.form');

const signupBtn = document.querySelector('.signupbtn');
const anchors = document.querySelectorAll('a');

anchors.forEach(anchor => {
    anchor.addEventListener('click', () => {
        const id = anchor.id;
        switch(id) {
            case 'loginLabel':
            case 'loginLabel2':
                signupForm.style.display = 'none';
                loginForm.style.display = 'block';
                forgotForm.style.display = 'none';
                break;
            case 'signupLabel':
            case 'signupLabel2':
                signupForm.style.display = 'block';
                loginForm.style.display = 'none';
                forgotForm.style.display = 'none';
                break;
            case 'forgotLabel':
                signupForm.style.display = 'none';
                loginForm.style.display = 'none';
                forgotForm.style.display = 'block';
                break;
        }
    });
});

// Manejo del registro
signupBtn.addEventListener('click', () => {
    const name = document.querySelector('#name').value;
    const username = document.querySelector('#username').value;
    const email = document.querySelector('#email').value.trim();
    const password = document.querySelector('#password').value;

    auth.createUserWithEmailAndPassword(email, password)
        .then((userCredential) => {
            const user = userCredential.user;
            const uid = user.uid;
            user.sendEmailVerification()
                .then(() => {

                })
                .catch((error) => {

                });
            console.log('User data saved to Firestore');
            firestore.collection('users').doc(uid).set({
                name: name,
                username: username,
                email: email,
            });
            signupForm.style.display = 'none';
            loginForm.style.display = 'block';
            forgotForm.style.display = 'none';
        })
        .catch((error) => {
            alert('Error signing up: ' + error.message);
        });
});

// Manejo del inicio de sesión
const loginBtn = document.querySelector('.loginbtn');
loginBtn.addEventListener('click', () => {
    const email = document.querySelector('#inUsr').value.trim();
    const password = document.querySelector('#inPass').value;

    auth.signInWithEmailAndPassword(email, password)
        .then((userCredential) => {
            const user = userCredential.user;
            console.log('User is signed in:', user);
            location.href = "/productos"; // Redirigir a la URL correcta para mostrar productos
        })
        .catch((error) => {
            alert('Error signing in: ' + error.message);
        });

// Manejo de la recuperación de contraseña
    const forgotBtn = document.querySelector('.forgotbtn');
    forgotBtn.addEventListener('click', () => {
        const emailForReset = document.querySelector('#forgotinp').value.trim();
        if (emailForReset.length > 0) {
            auth.sendPasswordResetEmail(emailForReset)
                .then(() => {
                    alert('Password reset email sent. Please check your inbox to reset your password.');
                    signupForm.style.display = 'none';
                    loginForm.style.display = 'block';
                    forgotForm.style.display = 'none';
                })
                .catch((error) => {
                    alert('Error sending password reset email: ' + error.message);
                });
        }
    });
});