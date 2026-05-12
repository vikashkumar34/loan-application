import React, { useState, useEffect, useRef } from 'react';
import { FaCommentDots, FaTimes, FaPaperPlane } from 'react-icons/fa';

export default function Chatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(scrollToBottom, [messages]);

  useEffect(() => {
    if (isOpen) {
      // Initial greeting and menu
      setMessages([
        { from: 'bot', text: 'Hello! I am your smart assistant. How can I help you today?' },
        { from: 'bot', text: 'You can ask me about: \n1. New Loan Enquiry \n2. Existing Loan Status \n3. How to improve CIBIL score \n4. Interest Rates' }
      ]);
    }
  }, [isOpen]);

  const handleSend = () => {
    if (input.trim() === '') return;

    const userMessage = { from: 'user', text: input };
    const newMessages = [...messages, userMessage];

    // Get bot response
    const botResponse = getBotResponse(input);
    newMessages.push({ from: 'bot', text: botResponse });

    setMessages(newMessages);
    setInput('');
  };

  const getBotResponse = (userInput) => {
    const lowerInput = userInput.toLowerCase();

    if (lowerInput.includes('new loan')) {
      return 'You can apply for a new loan directly from your dashboard! Just click the "Apply for New Loan" button to get started. We offer Personal, Home, and Education loans.';
    }
    if (lowerInput.includes('status')) {
      return 'You can see the real-time status of all your loan applications right here on your dashboard. Each application has a status like SUBMITTED, APPROVED, or DISBURSED.';
    }
    if (lowerInput.includes('cibil') || lowerInput.includes('score')) {
      return 'Improving your CIBIL score is a great goal! Key factors include: \n- Paying all your bills and loan EMIs on time. \n- Keeping your credit card balances low. \n- Avoiding multiple new credit applications in a short period. \nA score above 750 is generally considered excellent and can get you better interest rates!';
    }
    if (lowerInput.includes('interest') || lowerInput.includes('rate')) {
      return 'Interest rates vary based on loan type, your annual income, and your CIBIL score. For example, for a Personal Loan, a higher income and a CIBIL score above 750 will get you the best rates, starting from 10%. You can see the calculated rate when you fill out the new loan application form.';
    }
    if (lowerInput.includes('help')) {
      return 'You can ask me about: \n1. New Loan Enquiry \n2. Existing Loan Status \n3. How to improve CIBIL score \n4. Interest Rates';
    }

    return "I'm sorry, I didn't understand that. You can ask for 'help' to see what I can do.";
  };

  if (!isOpen) {
    return (
      <button onClick={() => setIsOpen(true)} className="fixed bottom-8 right-8 bg-blue-600 text-white p-4 rounded-full shadow-lg hover:bg-blue-700">
        <FaCommentDots size={24} />
      </button>
    );
  }

  return (
    <div className="fixed bottom-8 right-8 w-96 h-[600px] bg-white rounded-lg shadow-2xl flex flex-col">
      <div className="p-4 bg-blue-600 text-white flex justify-between items-center rounded-t-lg">
        <h3 className="font-bold">Smart Help Chat</h3>
        <button onClick={() => setIsOpen(false)}><FaTimes /></button>
      </div>
      <div className="flex-1 p-4 overflow-y-auto bg-gray-50">
        {messages.map((msg, index) => (
          <div key={index} className={`my-2 ${msg.from === 'bot' ? 'text-left' : 'text-right'}`}>
            <div className={`inline-block p-3 rounded-lg ${msg.from === 'bot' ? 'bg-gray-200 text-gray-800' : 'bg-blue-500 text-white'}`}>
              {msg.text.split('\n').map((line, i) => <p key={i}>{line}</p>)}
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>
      <div className="p-4 border-t flex">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSend()}
          placeholder="Type your message..."
          className="flex-1 p-2 border rounded-l-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button onClick={handleSend} className="bg-blue-600 text-white p-3 rounded-r-lg hover:bg-blue-700">
          <FaPaperPlane />
        </button>
      </div>
    </div>
  );
}
