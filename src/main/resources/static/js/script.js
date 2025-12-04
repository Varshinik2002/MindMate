// --- Set API URL dynamically ---
const API_BASE_URL =
  window.location.hostname === "localhost"
    ? "http://localhost:8080"
    : "https://mindmate-ti01.onrender.com"; // <-- replace with your backend Render URL

// --- Chat Logic ---
async function sendMessage() {
  const input = document.getElementById("userInput");
  const chatBox = document.getElementById("chatBox");

  const userText = input.value.trim();
  if (userText === "") return;

  const email = localStorage.getItem("email");

  // Show user message
  chatBox.innerHTML += `<p class='user'>You: ${userText}</p>`;

  try {
    const response = await fetch(`${API_BASE_URL}/api/chat/send`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ email: email, message: userText })
    });

    if (!response.ok) throw new Error("Server not responding");

    const data = await response.json();
    const botReply = data.reply;

    // Display bot message
    chatBox.innerHTML += `<p class='bot'>Bot: ${botReply}</p>`;

  } catch (err) {
    chatBox.innerHTML += `<p class='bot error'>Bot: Sorry, I’m having trouble connecting. 😔</p>`;
    console.error("Error:", err);
  }

  input.value = "";
  chatBox.scrollTop = chatBox.scrollHeight;
}

// --- Load Previous Chat ---
window.onload = async () => {
  const email = localStorage.getItem("email");
  const chatBox = document.getElementById("chatBox");

  // Load mood
  const lastMood = localStorage.getItem("lastMood");
  if (lastMood) {
    document.getElementById("moodResponse").innerText =
      `Last time you felt: ${lastMood}. How are you today? 🌼`;
  }

  if (email) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/chat/history?email=${email}`);
      const history = await res.json();

      history.forEach(msg => {
        if (msg.sender === "user") {
          chatBox.innerHTML += `<p class='user'>You: ${msg.message}</p>`;
        } else {
          chatBox.innerHTML += `<p class='bot'>Bot: ${msg.message}</p>`;
        }
      });
    } catch (err) {
      console.error("Error loading chat history:", err);
    }
  }

  chatBox.scrollTop = chatBox.scrollHeight;
};
