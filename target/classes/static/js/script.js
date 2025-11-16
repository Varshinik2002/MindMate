// --- Chat Logic ---
async function sendMessage() {
  const input = document.getElementById("userInput");
  const chatBox = document.getElementById("chatBox");

  const userText = input.value.trim();
  if (userText === "") return;

  const email = localStorage.getItem("email"); // ⭐ required

  // Show user message
  chatBox.innerHTML += `<p class='user'>You: ${userText}</p>`;

  try {
    const response = await fetch("http://localhost:8080/api/chat/send", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        email: email,     // ⭐ SEND EMAIL TO BACKEND
        message: userText
      })
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


// --- Load Previous Chat From DATABASE ---
window.onload = async () => {
  const email = localStorage.getItem("email");
  const chatBox = document.getElementById("chatBox");

  // Load mood (unchanged)
  const lastMood = localStorage.getItem("lastMood");
  if (lastMood) {
    document.getElementById("moodResponse").innerText =
      `Last time you felt: ${lastMood}. How are you today? 🌼`;
  }

  // ⭐ Load chat from backend
  if (email) {
    try {
      const res = await fetch(`http://localhost:8080/api/chat/history?email=${email}`);
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
