function StateCard({ title, value, color, borderColor }) {
    return (
        <div
            className="flex-1 p-6 shadow-sm rounded-2xl"
            style={{
                backgroundColor: color,
                border: `1px solid ${borderColor}`
            }}
        >
            <div className="text-[#747373] font-semibold text-sm mb-3">
                {title}
            </div>

            <div className="text-2xl font-semibold">
                {value}
            </div>
        </div>
    );
}

export default StateCard;