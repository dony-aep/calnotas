class FieldData {
  final String name;
  final String percentage;
  final String grade;

  FieldData({
    required this.name,
    required this.percentage,
    required this.grade,
  });

  Map<String, dynamic> toJson() => {
    'name': name,
    'percentage': percentage,
    'grade': grade,
  };

  factory FieldData.fromJson(Map<String, dynamic> json) => FieldData(
    name: json['name'] ?? '',
    percentage: json['percentage'] ?? '',
    grade: json['grade'] ?? '',
  );
}
