import ProblemSection from "../components/molecules/ProblemSection";
import CodeEditorSection from "../components/molecules/CodeEditorSection";

function SolvePage() {
    return (
        <div className="grid grid-rows-[auto_1fr] h-screen">
            <header className="bg-gray-700 text-white p-4 text-xl font-bold">
                피보나치 수열
            </header>
            <main className="grid grid-cols-2 h-full">
                <ProblemSection />
                <CodeEditorSection />
            </main>
        </div>
    );
}

export default SolvePage;
